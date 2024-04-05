package de.miraculixx.mutils.module

import de.miraculixx.challenge.api.MWorldAPI
import de.miraculixx.challenge.api.data.CustomGameRule
import de.miraculixx.challenge.api.data.GeneratorDefaults
import de.miraculixx.challenge.api.data.GeneratorProviderData
import de.miraculixx.challenge.api.data.WorldData
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mutils.MWorlds
import de.miraculixx.mutils.data.ChunkCalcData
import de.miraculixx.mutils.data.getGenerator
import de.miraculixx.mutils.data.getProvider
import de.miraculixx.mutils.settings
import de.miraculixx.mvanilla.extensions.readJsonString
import de.miraculixx.mvanilla.messages.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.kyori.adventure.util.TriState
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.io.File
import java.util.*

object WorldManager : MWorldAPI() {
    init {
        instance = this
    }

    private val customWorlds: MutableMap<UUID, WorldData> = mutableMapOf()
    private val saveFile = File("${MWorlds.configFolder.path}/worlds.json")

    override fun getWorldData(uuid: UUID): WorldData? = customWorlds[uuid]

    override fun getLoadedWorlds() = customWorlds.toMap()

    override fun createWorld(worldData: WorldData): UUID? {
        val world = WorldCreator(worldData.worldName).apply {
            try {
                environment(World.Environment.valueOf(worldData.environment.name))
                type(WorldType.valueOf(worldData.worldType.name))
            } catch (_: IllegalArgumentException) {
                return null
            }
            worldData.seed?.let { seed(it) }
            val biomeInfo = worldData.biomeProvider
            biomeInfo.algorithm.getProvider(biomeInfo.settings)?.let { biomeProvider(it) }
            val generatorClass = InternalChunkGenerator(worldData.chunkDefaults, worldData.chunkProviders)
            generator(generatorClass)

            keepSpawnLoaded(TriState.FALSE) // Prevent initializing lag
        }.createWorld()

        if (world == null)
            console.sendMessage(prefix + cmp("Failed to generate world ${worldData.presetName} (${worldData.presetName})", cError))
        else {
            worldData.seed = world.seed
            customWorlds[world.uid] = worldData
        }

        // Apply special game rules
        if (worldData.customGameRules[CustomGameRule.BLOCK_UPDATES] == false) world?.uid?.let { CustomGameRuleListener.blockedPhysics.add(it) }
        return world?.uid
    }

    override fun copyWorld(worldID: UUID, name: String): UUID? {
        val sourceWorld = Bukkit.getWorld(worldID) ?: return null
        val newWorld = WorldCreator(name)
            .copy(sourceWorld)
            .createWorld()

        if (newWorld == null)
            console.sendMessage(prefix + cmp("Failed to generate world $name as copy from ${sourceWorld.name}", cError))
        else {
            val newData = getWorldData(sourceWorld.uid)?.copy()?.apply { seed = sourceWorld.seed }
            if (newData == null) {
                console.sendMessage(prefix + cmp("Failed to resolve data from world ${sourceWorld.name}!", cError))
                return null
            }
            customWorlds[newWorld.uid] = newData
        }
        return newWorld?.uid
    }

    override fun fullCopyWorld(worldID: UUID, name: String): UUID? {
        val sourceWorld = Bukkit.getWorld(worldID) ?: return null
        val source = sourceWorld.worldFolder
        val target = File(name)
        target.mkdir()

        source.listFiles()?.forEach {
            val fileName = it.name
            if (fileName == "session.lock" || fileName == "uid.dat") return@forEach
            if (!it.copyRecursively(File("${target.path}/${fileName}"), true))
                console.sendMessage(prefix + cmp("Failed to copy folder ${source.path} to ${target.path}!", cError))
        }

        return copyWorld(sourceWorld.uid, name)
    }

    override fun deleteWorld(worldID: UUID) {
        val sourceWorld = Bukkit.getWorld(worldID) ?: return
        val mainSpawn = worlds[0].spawnLocation
        sourceWorld.livingEntities.forEach { entity ->
            if (entity is Player) {
                entity.teleport(mainSpawn)
                entity.playSound(entity, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                entity.sendMessage(prefix + msg("event.teleportFromDelete", listOf(sourceWorld.name)))
            }
        }
        Bukkit.unloadWorld(sourceWorld, false)

        task(false, 0, 20 * 5, 3) {
            if (sourceWorld.worldFolder.deleteRecursively()) {
                customWorlds.remove(sourceWorld.uid)
                it.cancel()
            } else console.sendMessage(prefix + cmp("Failed to delete world ${sourceWorld.name}. ${it.counterDownToOne} trys left..."))
        }
    }

    fun save() {
        if (!saveFile.exists()) saveFile.parentFile.mkdirs()
        saveFile.writeText(json.encodeToString(customWorlds.values.toList()))
    }

    fun load() {
        customWorlds.clear()
        val output = saveFile.readJsonString(false)
        taskRunLater(1) {
            json.decodeFromString<List<WorldData>>(output).forEach { worldData ->
                consoleAudience.sendMessage(prefix + cmp("Loading world ${worldData.worldName} from preset ${worldData.presetName}..."))
                val world = createWorld(worldData)
                if (world == null) consoleAudience.sendMessage(prefix + cmp("Failed to load world ${worldData.worldName}!", cError))
                else customWorlds[world] = worldData
            }
        }
    }

    private fun schedule() {
        val intervall = settings.getInt("save-intervall").coerceAtLeast(1)
        val time = 20L * 60 * intervall
        task(false, time, time) {
            console.sendMessage(prefix + cmp("Saving all temporary data..."))
            save()
            WorldDataHandling.saveAllPlayer()
            WorldDataHandling.saveAll()
            console.sendMessage(prefix + cmp("Successfully saved all data!"))
        }
    }

    init {
        schedule()
    }

    private class InternalChunkGenerator(
        private val defaults: GeneratorDefaults,
        private val settings: List<GeneratorProviderData>,
    ) : ChunkGenerator() {
        override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
            val chunkInfo = ChunkCalcData(chunkX, chunkZ, chunkData)
            settings.forEach {
                it.algorithm.getGenerator(it.settings).invoke(chunkInfo)
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