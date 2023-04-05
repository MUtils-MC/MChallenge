package de.miraculixx.mutils.module

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.runnables.async
import de.miraculixx.mutils.data.ChunkCalcData
import de.miraculixx.api.data.GeneratorProviderData
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mutils.data.getGenerator
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.generator.ChunkGenerator
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import org.bukkit.material.MaterialData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.awt.Color

class MapRender(private val player: Player, private val inv: CustomInventory, generators: List<GeneratorProviderData>) {
    private val previousInventory: Array<ItemStack?> = player.inventory.contents
    private val bar = BossBar.bossBar(msg("event.sneakExit"), 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS)

    /**
     * Pull back the player in the inventory if sneaking
     */
    private val onSneak = listen<PlayerToggleSneakEvent> {
        if (it.player != player) return@listen
        it.isCancelled = true
        exitMapView(true)
    }

    /**
     * Prevent player from clicking inside their own inventory
     */
    private val onInvClick = listen<InventoryClickEvent> {
        if (it.whoClicked != player) return@listen
        it.isCancelled = true
    }

    /**
     * Prevent player from moving around their head (and location)
     */
    private val onMove = listen<PlayerMoveEvent> {
        if (it.player != player) return@listen
        it.isCancelled = true
    }

    /**
     * Abort setup if player leaves
     */
    private val onLeave = listen<PlayerQuitEvent> {
        if (it.player != player) return@listen
        exitMapView(false)
    }

    /**
     * Prevents player from not holding the map
     */
    private val onF = listen<PlayerSwapHandItemsEvent> {
        if (it.player != player) return@listen
        it.isCancelled = true
    }

    private fun exitMapView(openGUI: Boolean) {
        player.inventory.contents = previousInventory
        onSneak.unregister()
        onInvClick.unregister()
        onMove.unregister()
        onLeave.unregister()
        onF.unregister()
        player.removePotionEffect(PotionEffectType.BLINDNESS)
        player.removePotionEffect(PotionEffectType.INVISIBILITY)
        player.hideBossBar(bar)
        if (openGUI) inv.open(player)
    }

    private fun prerenderMap(generators: List<GeneratorProviderData>): Boolean {
        val pixelMap = (1..128).map { (1..128).map { true }.toTypedArray() }.toTypedArray()
        for (x in 0..7) {
            for (z in 0..7) {
                val chunkInfo = ChunkCalcData(x, z, MapRenderChunk(x, z, pixelMap))
                generators.forEach {
                    it.algorithm.getGenerator(it.settings).invoke(chunkInfo)
                }
            }
        }

        val map = itemStack(Material.FILLED_MAP) {
            meta<MapMeta> {
                mapView = Bukkit.createMap(worlds[0])
                val view = mapView
                if (view == null) {
                    if (debug) console.sendMessage(prefix + cmp("Failed to resolve MapView on prerender Map!"))
                    return false
                }

                view.renderers.forEach { r -> view.removeRenderer(r) }
                view.addRenderer(CustomMapRenderer(rotateRight(pixelMap)))
                view.addRenderer(CustomMapOverlay())
            }
        }
        val inv = player.inventory
        (0..8).forEach { slot ->
            inv.setItem(slot, map)
        }
        return true
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

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    class MapRenderChunk(
        private val chunkX: Int,
        private val chunkZ: Int,
        private val pixelMap: Array<Array<Boolean>>,
    ): ChunkGenerator.ChunkData {
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
            return -64
        }
        override fun getMaxHeight(): Int {
            return 319
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

    init {
        player.closeInventory()
        player.inventory.clear()
        player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 99999, 1, false, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 99999, 1, false, false, false))
        player.teleport(player.location.apply { pitch = 90f })
        player.showBossBar(bar)
        async { prerenderMap(generators) }
    }
}