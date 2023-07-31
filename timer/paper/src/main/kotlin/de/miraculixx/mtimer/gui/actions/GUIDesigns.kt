package de.miraculixx.mtimer.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mtimer.MTimer
import de.miraculixx.mtimer.data.TimerDesign
import de.miraculixx.mtimer.data.TimerPresets
import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.items.ItemsDesignEditor
import de.miraculixx.mtimer.gui.items.ItemsOverview
import de.miraculixx.mtimer.vanilla.data.TimerGUI
import de.miraculixx.mtimer.vanilla.module.Timer
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mvanilla.extensions.*
import de.miraculixx.mvanilla.messages.*
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.io.File
import java.util.*

class GUIDesigns(private val isPersonal: Boolean, private val timer: Timer) : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem

        when (item?.itemMeta?.customModel ?: 0) {
            0 -> {
                player.closeInventory()
                player.click()
                val id = if (isPersonal) player.uniqueId.toString() else "TIMER_GLOBAL"
                TimerGUI.OVERVIEW.buildInventory(player, id, ItemsOverview(timer, isPersonal), GUIOverview(isPersonal))
            }

            1 -> {
                player.closeInventory()
                player.click()
                val preset = TimerPresets.PRESET
                val design = preset.design
                val uuid = UUID.randomUUID()
                design.owner = player.name
                TimerManager.addDesign(design, uuid)
                TimerGUI.DESIGN_EDITOR.buildInventory(player, player.uniqueId.toString(), ItemsDesignEditor(design, uuid), GUIDesignEditor(design, uuid, isPersonal))
            }

            2 -> { // TODO
//                player.closeInventory()
//                val bridge = MTimer.bridgeAPI
//                if (bridge == null) {
//                    player.soundError()
//                    player.sendMessage(
//                        prefix + cmp("The public library is not implemented yet!\nCheckout ", cError) +
//                                cmp("MUtils.de", cError, underlined = true).clickEvent(ClickEvent.openUrl("https://mutils.de")) +
//                                cmp(" for more information", cError)
//                    )
////                    player.sendMessage(msgNoBridge)
//                } else {
//                    TODO()
//                    //Open GUI
//                }
            }

            10 -> {
                when (it.click) {
                    ClickType.LEFT -> {
                        timer.design = item.getDesign(player)?.first ?: return@event
                        player.soundEnable()
                        inv.update()
                    }

                    ClickType.RIGHT -> {
                        val design = item.getDesign(player) ?: return@event
                        player.click()
                        player.closeInventory()
                        TimerGUI.DESIGN_EDITOR.buildInventory(
                            player, player.uniqueId.toString(),
                            ItemsDesignEditor(design.first, design.second), GUIDesignEditor(design.first, design.second, isPersonal)
                        )
                    }

                    ClickType.SHIFT_RIGHT -> {
                        if (item?.enchantments?.isNotEmpty() == true) {
                            player.soundError()
                            return@event
                        }
                        val design = item.getDesign(player) ?: return@event
                        TimerManager.removeDesign(design.second, File("${MTimer.configFolder}/designs"))
                        player.soundDelete()
                        inv.update()
                    }

                    else -> player.soundStone()
                }
            }
        }
    }

    private fun ItemStack?.getDesign(player: Player): Pair<TimerDesign, UUID>? {
        val uuidString = this?.itemMeta?.persistentDataContainer?.get(NamespacedKey(namespace, "gui.timer.design"), PersistentDataType.STRING) ?: return null
        val uuid = try {
            UUID.fromString(uuidString)
        } catch (_: IllegalArgumentException) {
            player.soundError()
            return null
        }
        val design = TimerManager.getDesign(uuid)
        return if (design == null) {
            player.soundError()
            null
        } else design to uuid
    }
}