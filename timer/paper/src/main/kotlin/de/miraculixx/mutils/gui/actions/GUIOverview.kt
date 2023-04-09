package de.miraculixx.mutils.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mutils.gui.TimerGUI
import de.miraculixx.mutils.gui.items.ItemsDesigns
import de.miraculixx.mutils.gui.items.ItemsGoals
import de.miraculixx.mutils.gui.items.ItemsRules
import de.miraculixx.mutils.module.TimerManager
import de.miraculixx.mvanilla.extensions.click
import de.miraculixx.mvanilla.extensions.soundDisable
import de.miraculixx.mvanilla.extensions.soundEnable
import de.miraculixx.mvanilla.messages.emptyComponent
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class GUIOverview(private val isPersonal: Boolean) : GUIEvent {
    private val noPersonalTimer = prefix + msg("event.noPersonalTimer")

    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem
        val timer = if (isPersonal) TimerManager.getPersonalTimer(player.uniqueId) else TimerManager.getGlobalTimer()
        if (timer == null) {
            player.sendMessage(noPersonalTimer)
            return@event
        }
        when (val id = item?.itemMeta?.customModel ?: 0) {
            5 -> {
                player.closeInventory()
                val guiID = if (isPersonal) player.uniqueId.toString() else "TIMER_GLOBAL_DESIGNS"
                player.click()
                TimerGUI.DESIGN.buildInventory(player, guiID, ItemsDesigns(timer), GUIDesigns(isPersonal, timer))
            }

            6 -> if (timer.countUp) {
                timer.countUp = false
                player.soundDisable()
            } else {
                timer.countUp = true
                player.soundEnable()
            }

            7 -> if (timer.visible) {
                timer.visible = false
                player.soundDisable()
                player.sendActionBar(emptyComponent())
            } else {
                timer.visible = true
                player.soundEnable()
            }

            8 -> {
                player.closeInventory()
                player.click()
                TimerGUI.RULES.buildInventory(player, player.uniqueId.toString(), ItemsRules(), GUIRules())
                return@event
            }

            9 -> {
                player.closeInventory()
                player.click()
                TimerGUI.GOALS.buildInventory(player, player.uniqueId.toString(), ItemsGoals(), GUIGoals())
                return@event
            }

            1, 2, 3, 4 -> {
                // Time Settings
                val timeAdded = when (it.click) {
                    ClickType.LEFT -> {
                        when (id) {
                            1 -> timer.addTime(sec = 1)
                            2 -> timer.addTime(min = 1)
                            3 -> timer.addTime(hour = 1)
                            4 -> timer.addTime(day = 1)
                            else -> false
                        }
                    }

                    ClickType.RIGHT -> {
                        when (id) {
                            1 -> timer.addTime(sec = -1)
                            2 -> timer.addTime(min = -1)
                            3 -> timer.addTime(hour = -1)
                            4 -> timer.addTime(day = -1)
                            else -> false
                        }
                    }

                    ClickType.SHIFT_LEFT -> {
                        when (id) {
                            1 -> timer.addTime(sec = 10)
                            2 -> timer.addTime(min = 10)
                            3 -> timer.addTime(hour = 10)
                            4 -> timer.addTime(day = 10)
                            else -> false
                        }

                    }

                    ClickType.SHIFT_RIGHT -> {
                        when (id) {
                            1 -> timer.addTime(sec = -10)
                            2 -> timer.addTime(min = -10)
                            3 -> timer.addTime(hour = -10)
                            4 -> timer.addTime(day = -10)
                            else -> false
                        }
                    }

                    else -> false
                }

                if (timeAdded) player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1.5f)
                else player.playSound(player, Sound.BLOCK_STONE_FALL, 1f, 1f)
            }

            else -> return@event
        }

        inv.update()
    }
}