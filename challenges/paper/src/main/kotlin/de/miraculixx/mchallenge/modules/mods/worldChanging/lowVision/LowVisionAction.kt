package de.miraculixx.mchallenge.modules.mods.worldChanging.lowVision

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mchallenge.global.ResourcePackChallenge
import de.miraculixx.mcore.await.AwaitChatMessage
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mvanilla.extensions.soundDisable
import de.miraculixx.mvanilla.extensions.soundEnable
import de.miraculixx.mvanilla.extensions.soundError
import de.miraculixx.mvanilla.extensions.soundUp
import de.miraculixx.mvanilla.messages.consoleAudience
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mvanilla.messages.prefix
import de.miraculixx.mweb.api.MWebAPI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import java.io.File

class LowVisionAction(private val maxSelects: Int, private val player: Player, private val mWebAPI: MWebAPI) : GUIEvent, ResourcePackChallenge {
    private val texturepackFolder = File(MChallenge.configFolder, "data/resourcepacks/${player.uniqueId}-LOWVISION")
    private val resourceFolder = createRPStructure(texturepackFolder)
    private val blockstateFolder = File(resourceFolder, "blockstates")
    private val blockstateScript = "{\"variants\":{\"\":{\"model\":\"block/transparent_all\"}}}"

    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem
        val meta = item?.itemMeta ?: return@event
        val itemProvider = inv.itemProvider as LowVisionItems

        when (meta.customModel) {
            10 -> {
                AwaitChatMessage(false, player, "Filter", 30, null, false, msg("event.enterFilter", listOf("blocks")), {
                    itemProvider.filter = it.replace(' ', '_')
                    player.soundEnable()
                }) {
                    inv.update()
                    inv.open(player)
                }
            }

            1 -> {
                val material = item.type
                if (itemProvider.selected.add(material)) player.soundEnable()
                else {
                    itemProvider.selected.remove(material)
                    player.soundDisable()
                }
                inv.update()
                if (itemProvider.selected.size >= maxSelects) {
                    player.closeInventory()
                    player.soundUp()
                    createResourcePack(itemProvider.selected)
                }
            }
        }
    }

    override val close: ((InventoryCloseEvent, CustomInventory) -> Unit) = event@{ it: InventoryCloseEvent, inv: CustomInventory ->
        if (it.reason == InventoryCloseEvent.Reason.PLUGIN) return@event
        taskRunLater(2) {
            inv.open(it.player as? Player ?: return@taskRunLater)
            player.soundError()
        }
    }

    private fun createResourcePack(selected: Set<Material>) {
        val blockModelFolder = File(resourceFolder, "models/block")
        blockModelFolder.mkdir()
        File(blockModelFolder, "transparent_all.json").writeText("{\"parent\":\"block/transparent\",\"textures\":{\"particle\":\"#all\",\"down\":\"#all\",\"up\":\"#all\",\"north\":\"#all\",\"east\":\"#all\",\"south\":\"#all\",\"west\":\"#all\"}}")
        File(blockModelFolder, "transparent.json").writeText("{\"parent\":\"block/block\",\"textures\":{\"all\":\"block/transparent\"}}")
        File(resourceFolder, "textures/block").mkdir()
        transparency?.readBytes()?.let { File(resourceFolder, "textures/block/transparent.png").writeBytes(it) }

        val blocks = Material.values().filter { it.isBlock }.toMutableSet()
        blocks.removeAll(selected)
        blocks.forEach {
            val name = it.name.lowercase()
            if (it == Material.WATER) {
                val transparency = transparency?.readBytes() ?: return
                File(resourceFolder, "textures/block/water_flow.png").writeBytes(transparency)
                File(resourceFolder, "textures/block/water_still.png").writeBytes(transparency)
                File(resourceFolder, "textures/block/water_overlay.png").writeBytes(transparency)
            } else File(blockstateFolder, "${name}.json").writeText(blockstateScript)
        }

        mWebAPI.sendFileAsResourcePack(texturepackFolder.path, setOf(player.uniqueId), true)
    }
}