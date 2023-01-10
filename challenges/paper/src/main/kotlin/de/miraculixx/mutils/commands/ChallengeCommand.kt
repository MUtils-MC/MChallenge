package de.miraculixx.mutils.commands

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.bukkit.register
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.mutils.enums.challenges.ChallengeStatus
import de.miraculixx.mutils.gui.items.skullTexture
import de.miraculixx.mutils.messages.msg
import de.miraculixx.mutils.messages.plus
import de.miraculixx.mutils.messages.prefix
import de.miraculixx.mutils.modules.ChallengeManager
import de.miraculixx.mutils.utils.gui.GUITypes
import de.miraculixx.mutils.utils.gui.actions.GUIChallenge
import de.miraculixx.mutils.utils.gui.items.ItemsChallenge
import org.bukkit.HeightMap
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.block.Biome
import org.bukkit.block.data.BlockData
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.ChunkGenerator.ChunkData
import org.bukkit.generator.WorldInfo
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import org.bukkit.material.MaterialData
import java.awt.Color
import java.util.*
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt

class ChallengeCommand : TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String> {
        return buildList {
            when (args?.size ?: 0) {
                0, 1 -> addAll(listOf("stop", "start", "pause", "resume"))
            }
        }.toMutableList()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (args == null || args.isEmpty()) {
            if (sender is Player) GUITypes.CHALLENGE_MENU.buildInventory(sender, sender.uniqueId.toString(), ItemsChallenge(), GUIChallenge())
            else sender.sendMessage(prefix + msg("command.noPlayer"))
            return true
        }

        when (args.getOrNull(0)?.lowercase()) {
            "stop" -> if (ChallengeManager.stopChallenges()) {
                ChallengeManager.status = ChallengeStatus.STOPPED
                broadcast((msg("command.challenge.stop", listOf(sender.name))))
            } else sender.sendMessage(prefix + msg("command.challenge.alreadyOff"))

            "start" -> if (ChallengeManager.status == ChallengeStatus.RUNNING) {
                sender.sendMessage(prefix + msg("command.challenge.alreadyOn"))
                return false
            } else if (ChallengeManager.startChallenges()) {
                ChallengeManager.status = ChallengeStatus.RUNNING
                broadcast(msg("command.challenge.start", listOf(sender.name)))
            } else sender.sendMessage(prefix + msg("command.challenge.failed"))

            "pause" -> if (ChallengeManager.unregisterChallenges()) {
                ChallengeManager.status = ChallengeStatus.PAUSED
                broadcast(msg("command.challenge.pause", listOf(sender.name)))
            } else sender.sendMessage(prefix + msg("command.challenge.alreadyOff"))

            "resume" -> if (ChallengeManager.registerChallenges()) {
                ChallengeManager.status = ChallengeStatus.RUNNING
                broadcast(msg("command.challenge.continue", listOf(sender.name)))
            } else sender.sendMessage(prefix + msg("command.challenge.alreadyOff"))

            "test" -> {
                val world = WorldCreator(UUID.randomUUID().toString())
                    .environment(World.Environment.THE_END)
                    .generator(SinusWorld(args[1].toInt(), args[2].toInt()))
                    .createWorld()
                (sender as Player).teleport(world!!.spawnLocation)
            }

            "test1" -> {
                val player = (sender as Player)
                val item = prerenderMap(args[1].toInt(), player.inventory.itemInMainHand)

            }

            "test2" -> {
                (sender as Player).inventory.addItem(itemStack(Material.PLAYER_HEAD) {
                    itemMeta = (itemMeta as SkullMeta).skullTexture(args[1])
                })
            }
        }
        return true
    }

    init {
        register("ch")
        register("challenge")
    }

    private val onWaterFlow = listen<BlockFromToEvent> {
        // TODO Check if world is custom
        if (it.block.chunk.inhabitedTime < 20*20) it.isCancelled = true
    }

    /**
     * @param zScratch Higher values produces wider circles - Scratched in z/x axe
     * @param yScratch Higher values produces higher tops - Scratched in y axe
     */
    class SinusWorld(private val zScratch: Int, private val yScratch: Int): ChunkGenerator() {
        override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
            val startX = chunkX * 16
            val startZ = chunkZ * 16

            for (x in 0..16) {
                for (z in 0..16) {
                    val realX = startX + x
                    val realZ = startZ + z
                    val dis = sqrt(.0 + realX * realX + realZ * realZ)
                    val halt = sin(dis / zScratch) * yScratch
                    chunkData.setRegion(x, halt.toInt(), z, x + 1, 319, z + 1, Material.AIR)
                }
            }
        }

        override fun getBaseHeight(worldInfo: WorldInfo, random: Random, x: Int, z: Int, heightMap: HeightMap): Int {
            val a = x / 16 * x / 16
            val b = z / 16 * z / 16
            val dis = sqrt(.0 + a + b)
            val halt = abs(sin(dis / 10) * 50)
            return halt.toInt()
        }

        override fun shouldGenerateNoise(): Boolean { return true }
        override fun shouldGenerateSurface(): Boolean { return true }
        override fun shouldGenerateCaves(): Boolean { return true }
        override fun shouldGenerateDecorations(): Boolean { return true }
        override fun shouldGenerateMobs(): Boolean { return true }
        override fun shouldGenerateStructures(): Boolean { return true }
    }

    class ChunkedHoles(private val xScale: Int, private val zScale: Int, holeMode: Boolean): ChunkGenerator() {

        override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
            chunkData.setRegion(xScale, -64, zScale, 16 - xScale, 319, 16 - zScale, Material.AIR)
        }

        override fun shouldGenerateNoise(): Boolean { return true }
        override fun shouldGenerateSurface(): Boolean { return true }
        override fun shouldGenerateCaves(): Boolean { return true }
        override fun shouldGenerateDecorations(): Boolean { return true }
        override fun shouldGenerateMobs(): Boolean { return true }
        override fun shouldGenerateStructures(): Boolean { return true }
    }

    class DummyChunkRender(private val xScale: Int): ChunkGenerator() {

        override fun generateNoise(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
           chunkRender(chunkX, chunkZ, xScale, chunkData)
        }

        override fun shouldGenerateNoise(): Boolean { return true }
        override fun shouldGenerateSurface(): Boolean { return true }
        override fun shouldGenerateCaves(): Boolean { return true }
        override fun shouldGenerateDecorations(): Boolean { return true }
        override fun shouldGenerateMobs(): Boolean { return true }
        override fun shouldGenerateStructures(): Boolean { return true }
    }

    private fun prerenderMap(xScale: Int, itemStack: ItemStack) {
        val pixelMap = (1..128).map { (1..128).map { true }.toTypedArray() }.toTypedArray()
        for (x in 0..7) {
            for (z in 0..7) {
                val chunkData = MapRenderChunk(x, z, pixelMap)
                chunkRender(x, z, xScale, chunkData)
            }
        }

        itemStack.editMeta {
            val mapMeta = it as MapMeta
            val view = mapMeta.mapView

            if (view == null) {
                println("MapView is null?")
                return@editMeta
            }

            view.renderers.forEach { r ->
                println("Remove a renderer from map")
                view.removeRenderer(r)
            }
            view.addRenderer(CustomMapRenderer(rotateRight(pixelMap)))
            view.addRenderer(CustomMapOverlay())
        }
    }

    private fun rotateRight(arr: Array<Array<Boolean>>): Array<Array<Boolean>> {
        val rows = arr.size
        val cols = arr[0].size
        val result = (1..128).map { (1..128).map { false }.toTypedArray() }.toTypedArray()

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                result[j][rows - 1 - i] = arr[i][j]
            }
        }

        return result
    }

    class CustomMapRenderer(private val pixelMap: Array<Array<Boolean>>): MapRenderer() {
        override fun render(map: MapView, canvas: MapCanvas, player: Player) {

            pixelMap.forEachIndexed { x, innerMap ->
                innerMap.forEachIndexed { z, colorValue ->
                    if (colorValue) canvas.setPixelColor(x, z, Color(42, 118, 30))
                }
            }
        }
    }

    class CustomMapOverlay: MapRenderer() {
        override fun render(map: MapView, canvas: MapCanvas, player: Player) {
            for (x in 0 .. 127) {
                canvas.setPixelColor(x, 127, getColor(x))
            }
            for (y in 0..127) {
                canvas.setPixelColor(0, 127 - y, getColor(y))
            }
        }

        private fun getColor(i: Int): Color {
            return when {
                i % 16 == 0 -> Color(0, 130, 255)
                i % 2 == 0 -> Color.WHITE
                else -> Color.BLACK
            }
        }
    }

    class MapRenderChunk(
        private val chunkX: Int,
        private val chunkZ: Int,
        private val pixelMap: Array<Array<Boolean>>,
        ): ChunkData {

        override fun setRegion(xMin: Int, yMin: Int, zMin: Int, xMax: Int, yMax: Int, zMax: Int, material: Material) {
            val isStone = material == Material.STONE
            val startX = chunkX * 16
            val startZ = chunkZ * 16

            (xMin until xMax.coerceAtMost(16)).forEach { dx ->
                (zMin until zMax.coerceAtMost(16)).forEach { dz ->
                    val x = startX + dx
                    val z = startZ + dz
                    pixelMap[x][z] = isStone
                }
            }
        }

        override fun getMinHeight(): Int {
            TODO("Not yet implemented")
        }
        override fun getMaxHeight(): Int {
            TODO("Not yet implemented")
        }
        override fun getBiome(x: Int, y: Int, z: Int): Biome {
            TODO("Not yet implemented")
        }
        override fun setBlock(x: Int, y: Int, z: Int, material: Material) {
            TODO("Not yet implemented")
        }
        override fun setBlock(x: Int, y: Int, z: Int, material: MaterialData) {
            TODO("Not yet implemented")
        }
        override fun setBlock(x: Int, y: Int, z: Int, blockData: BlockData) {
            TODO("Not yet implemented")
        }
        override fun setRegion(xMin: Int, yMin: Int, zMin: Int, xMax: Int, yMax: Int, zMax: Int, material: MaterialData) {
            TODO("Not yet implemented")
        }
        override fun setRegion(xMin: Int, yMin: Int, zMin: Int, xMax: Int, yMax: Int, zMax: Int, blockData: BlockData) {
            TODO("Not yet implemented")
        }
        override fun getType(x: Int, y: Int, z: Int): Material {
            TODO("Not yet implemented")
        }
        override fun getTypeAndData(x: Int, y: Int, z: Int): MaterialData {
            TODO("Not yet implemented")
        }
        override fun getBlockData(x: Int, y: Int, z: Int): BlockData {
            TODO()
        }
        override fun getData(x: Int, y: Int, z: Int): Byte {
            return 0
        }
    }
}

private fun chunkRender(chunkX: Int, chunkZ: Int, xScale: Int, chunkData: ChunkData) {
    val startX = chunkX * 16
    val startZ = chunkZ * 16
    val factor = xScale - 1

    for (x in 0..16) {
        for (z in 0..16) {
            val realX = startX + x
            val realZ = startZ + z
            val xOdd = (if (realX < 0) realX - factor else realX) / xScale % 2 == 0
            val zOdd = (if (realZ < 0) realZ - factor else realZ) / xScale % 2 == 0
            if (xOdd && zOdd || !xOdd && !zOdd) chunkData.setRegion(x, -64, z, x + 1, 319, z + 1, Material.AIR)
        }
    }
}