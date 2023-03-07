package de.miraculixx.mutils.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.MUtilsBridge
import de.miraculixx.mutils.data.TimerDesign
import de.miraculixx.mutils.data.TimerPresets
import de.miraculixx.mutils.extensions.*
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.TimerGUI
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.items.ItemsDesignEditor
import de.miraculixx.mutils.gui.items.ItemsOverview
import de.miraculixx.mutils.messages.msgNoBridge
import de.miraculixx.mutils.messages.namespace
import de.miraculixx.mutils.module.Timer
import de.miraculixx.mutils.module.TimerManager
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
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

            2 -> {
                player.closeInventory()
                val bridge = MUtilsBridge.INSTANCE
                if (bridge == null) {
                    player.soundError()
                    player.sendMessage(msgNoBridge)
                } else {
                    //Open GUI
                }
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
                        TimerGUI.DESIGN_EDITOR.buildInventory(player, player.uniqueId.toString(),
                            ItemsDesignEditor(design.first, design.second), GUIDesignEditor(design.first, design.second, isPersonal)
                        )
                    }

                    ClickType.SHIFT_RIGHT -> {
                        if (item?.enchantments?.isNotEmpty() == true) {
                            player.soundError()
                            return@event
                        }
                        val design = item.getDesign(player) ?: return@event
                        TimerManager.removeDesign(design.second)
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