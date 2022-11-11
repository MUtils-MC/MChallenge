@file:Suppress("NON_EXHAUSTIVE_WHEN")

package de.miraculixx.mutils.modules.gui

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.modules.timer.TimerSettings
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.tools.click
import net.axay.kspigot.items.customModel
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class TimerGUI(it: InventoryClickEvent, player: Player) {
    init {
        event(it, player)
    }

    fun event(it: InventoryClickEvent, p: Player) {
        val c = ConfigManager.getConfig(Configs.TIMER)
        val item = it.currentItem

        val id = item?.itemMeta?.customModel ?: 0
        when (id) {
            200 -> {
                GUIBuilder(p, GUI.SELECT_MENU, GUIAnimation.SPLIT).custom().open()
                p.click()
                return
            }
            5 -> {
                if (!GUIListener.verify(p)) return
                if (ModuleManager.isActive(Modules.TIMER)) {
                    ModuleManager.disableModule(Modules.TIMER)
                    p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.4f)
                } else {
                    ModuleManager.enableModule(Modules.TIMER)
                    p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
                }
                GUIBuilder(p, GUI.TIMER_SETTINGS).custom().open()
                return
            }
            6 -> {
                if (ModuleManager.timerSettings(TimerSettings.COUNT_UP) == true) {
                    ModuleManager.timerSettings(c, TimerSettings.COUNT_UP, i3 = false)
                    p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.4f)
                } else {
                    ModuleManager.timerSettings(c, TimerSettings.COUNT_UP, i3 = true)
                    p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
                }
                GUIBuilder(p, GUI.TIMER_SETTINGS).custom().open()
                return
            }
            8 -> {
                GUIBuilder(p, GUI.TIMER_DESIGN).custom().open()
                p.click()
                return
            }
            9 -> {
                if (!GUIListener.verify(p)) return
                GUIBuilder(p, GUI.TIMER_GOALS).scroll(0).open()
                p.click()
                return
            }
            7 -> {
                if (!GUIListener.verify(p)) return
                GUIBuilder(p, GUI.TIMER_RULES).scroll(0).open()
                p.click()
                return
            }
        }

        // Time Settings
        val timeAdded = when (it.click) {
            ClickType.LEFT -> {
                when (id) {
                    1 -> ModuleManager.addTime(sec = 1)
                    2 -> ModuleManager.addTime(min = 1)
                    3 -> ModuleManager.addTime(hour = 1)
                    4 -> ModuleManager.addTime(day = 1)
                    else -> false
                }
            }
            ClickType.RIGHT -> {
                when (id) {
                    1 -> ModuleManager.addTime(sec = -1)
                    2 -> ModuleManager.addTime(min = -1)
                    3 -> ModuleManager.addTime(hour = -1)
                    4 -> ModuleManager.addTime(day = -1)
                    else -> false
                }
            }
            ClickType.SHIFT_LEFT -> {
                when (id) {
                    1 -> ModuleManager.addTime(sec = 10)
                    2 -> ModuleManager.addTime(min = 10)
                    3 -> ModuleManager.addTime(hour = 10)
                    4 -> ModuleManager.addTime(day = 10)
                    else -> false
                }

            }
            ClickType.SHIFT_RIGHT -> {
                when (id) {
                    1 -> ModuleManager.addTime(sec = -10)
                    2 -> ModuleManager.addTime(min = -10)
                    3 -> ModuleManager.addTime(hour = -10)
                    4 -> ModuleManager.addTime(day = -10)
                    else -> false
                }
            }
            else -> false
        }
        if (timeAdded) p.playSound(p.location, Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1.5f)
        else p.playSound(p.location, Sound.BLOCK_STONE_FALL, 1f, 1f)
        GUIBuilder(p, GUI.TIMER_SETTINGS).custom().open()
    }
}