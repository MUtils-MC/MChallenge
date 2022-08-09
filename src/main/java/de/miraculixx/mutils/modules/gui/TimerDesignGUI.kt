package de.miraculixx.mutils.modules.gui

import de.miraculixx.mutils.enums.modules.timer.TimerDesign
import de.miraculixx.mutils.enums.modules.timer.TimerSettings
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class TimerDesignGUI(it: InventoryClickEvent, player: Player) {
    init {
        event(it, player)
    }

    fun event(it: InventoryClickEvent, player: Player) {
        //Click Checker
        val c = ConfigManager.getConfig(Configs.TIMER)
        val tool = GUITools(c)
        val item = it.currentItem

        when (item?.itemMeta?.customModelData) {
            200 -> {
                GUIBuilder(player, GUI.TIMER_SETTINGS).custom().open()
                player.click()
                return
            }
            1 -> {
                val style = when (ModuleManager.timerSettings(TimerSettings.DESIGN) as TimerDesign) {
                    TimerDesign.COMPACT -> TimerDesign.BRACKETS
                    TimerDesign.BRACKETS -> TimerDesign.PREFIX
                    TimerDesign.PREFIX -> TimerDesign.EXACT
                    TimerDesign.EXACT -> TimerDesign.COMPACT
                }
                ModuleManager.timerSettings(c, TimerSettings.DESIGN, i2 = style)
            }
            2 -> ModuleManager.timerSettings(
                c, TimerSettings.COLOR_PRIMARY,
                tool.colorRotate(ModuleManager.timerSettings(TimerSettings.COLOR_PRIMARY) as Char)
            )
            3 -> ModuleManager.timerSettings(
                c, TimerSettings.COLOR_SECONDARY,
                tool.colorRotate(ModuleManager.timerSettings(TimerSettings.COLOR_SECONDARY) as Char)
            )
            4 -> ModuleManager.timerSettings(
                c, TimerSettings.STYLE_PRIMARY,
                tool.styleRotate(ModuleManager.timerSettings(TimerSettings.STYLE_PRIMARY) as Char)
            )
            5 -> ModuleManager.timerSettings(
                c, TimerSettings.STYLE_SECONDARY,
                tool.styleRotate(ModuleManager.timerSettings(TimerSettings.STYLE_SECONDARY) as Char)
            )
        }
        player.click()
        GUIBuilder(player, GUI.TIMER_DESIGN).custom().open()
    }
}