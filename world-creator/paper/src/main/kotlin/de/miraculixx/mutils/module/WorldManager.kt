package de.miraculixx.mutils.module

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.extensions.geometry.add
import de.miraculixx.mutils.MWorlds
import de.miraculixx.mutils.data.*
import de.miraculixx.mutils.extensions.readJsonString
import de.miraculixx.mutils.messages.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.kyori.adventure.util.TriState
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.io.File
import java.util.*

object WorldManager {
    private val customWorlds: MutableMap<UUID, WorldData> = mutableMapOf()
    private val saveFile = File("${MWorlds.configFolder.path}/worlds.json")

    fun getWorldData(uuid: UUID): WorldData? {
        return customWorlds[uuid]
    }

    fun createWorld(worldData: WorldData): World? {
        val world = WorldCreator(worldData.worldName).apply {
            environment(worldData.environment)
            generateStructures(worldData.generateStructures)
            type(worldData.worldType)
            worldData.seed?.let { seed(it) }
            val biomeInfo = worldData.biomeProvider
            biomeInfo.algorithm.getProvider(biomeInfo.settings)?.let { biomeProvider(it) }
            val generatorClass = InternalChunkGenerator(worldData.chunkDefaults, worldData.chunkProviders)
            generator(generatorClass)

            keepSpawnLoaded(TriState.FALSE) // Prevent initializing lag
        }.createWorld()

        if (world == null)
            console.sendMessage(prefix + cmp("Failed to generate world ${worldData.presetName} (${worldData.presetName})", cError))
        return world
    }

    fun copyWorld(world: World, name: String): World? {
        val newWorld = WorldCreator(name)
            .copy(world)
            .createWorld()

        if (newWorld == null)
            console.sendMessage(prefix + cmp("Failed to generate world $name as copy from ${world.name}", cError))
        return newWorld
    }

    fun save() {
        if (!saveFile.exists()) saveFile.parentFile.mkdirs()
        saveFile.writeText(json.encodeToString(customWorlds.values))
    }

    fun load() {
        customWorlds.clear()
        val output = saveFile.readJsonString(false)
        json.decodeFromString<List<WorldData>>(output).forEach { worldData ->
            consoleAudience.sendMessage(prefix + cmp("Loading world ${worldData.worldName} from preset ${worldData.presetName}..."))
            val world = createWorld(worldData)
            if (world == null) consoleAudience.sendMessage(prefix + cmp("Failed to load world ${worldData.worldName}!", cError))
            else customWorlds[world.uid] = worldData
        }
    }

    /**
     * Prevent water/lava from flowing around on chunk generation. This will increase performance server and client side!
     */
    private val onWaterFlow = listen<BlockFromToEvent> {
        val block = it.toBlock
        if (!customWorlds.containsKey(block.world.uid)) return@listen
        if (block.chunk.inhabitedTime < 20) it.isCancelled = true
    }

    /**
     * Prevent sea-grass growing on near air positions to prevent block update mania (lag)
     */
    private val onGrow = listen<BlockGrowEvent> {
        val block = it.newState
        val world = block.world
        if (!customWorlds.containsKey(world.uid)) return@listen
        val sourceLoc = block.location
        for (x in -1..1) {
            for (z in -1..1) {
                if (world.getBlockAt(sourceLoc.blockX + x, sourceLoc.blockY, sourceLoc.blockZ + z).type == Material.AIR) {
                    it.isCancelled = true
                    return@listen
                }
            }
        }
    }

    private class InternalChunkGenerator(
        private val defaults: GeneratorDefaults,
        private val settings: List<GeneratorData>,
    ) : ChunkGenerator() {
        override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
            val chunkInfo = ChunkCalcData(worldInfo, chunkX, chunkZ, chunkData)
            settings.forEach {
                it.generator.getGenerator(it).invoke(chunkInfo)
            }
        }

        override fun shouldGenerateNoise(): Boolean {
            return defaults.vanillaNoise
        }

        override fun shouldGenerateSurface(): Boolean {
            return defaults.vanillaSurface
        }

        override fun shouldGenerateCaves(): Boolean {
            return defaults.vanillaCaves
        }

        override fun shouldGenerateDecorations(): Boolean {
            return defaults.vanillaFoliage
        }

        override fun shouldGenerateMobs(): Boolean {
            return defaults.vanillaMobs
        }

        override fun shouldGenerateStructures(): Boolean {
            return defaults.vanillaStructures
        }
    }
}