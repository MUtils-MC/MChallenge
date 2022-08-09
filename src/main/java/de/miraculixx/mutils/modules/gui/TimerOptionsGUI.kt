package de.miraculixx.mutils.modules.gui

import de.miraculixx.mutils.enums.modules.timer.DeathPunish
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIState
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class TimerOptionsGUI(it: InventoryClickEvent, player: Player, title: String) {
    init {
        event(it, player, title)
    }

    fun event(it: InventoryClickEvent, player: Player, title: String) {
        val inv = if (title.endsWith("Goals"))
            GUI.TIMER_GOALS
        else GUI.TIMER_RULES
        val c = ConfigManager.getConfig(Configs.TIMER)
        val tool = GUITools(c)
        val item = it.currentItem

        val click = it.click
        when (item?.itemMeta?.customModelData) {
            200 -> {
                GUIBuilder(player, GUI.TIMER_SETTINGS).custom().open()
                player.click()
                return
            }
            202 -> {
                val change = if (click.isShiftClick) -5
                else -1
                tool.navigate(player, change, GUI.TIMER_SETTINGS, GUIState.SCROLL)
                return
            }
            203 -> {
                val change = if (click.isShiftClick) 5
                else 1
                tool.navigate(player, change, GUI.TIMER_SETTINGS, GUIState.SCROLL)
                return
            }

            6 -> tool.toggleSetting(player,"Settings.Spec on Death")
            7 -> tool.toggleSetting(player,"Settings.Spec on Join")
            8 -> if (click.isLeftClick) tool.toggleSetting(player, "Settings.Punishment")
                else c["Settings.Death Punishment"] =
                when (DeathPunish.valueOf(c.getString("Settings.Death Punishment")?:"NOTHING")) {
                    DeathPunish.KICK -> DeathPunish.BAN.name
                    DeathPunish.BAN -> DeathPunish.NOTHING.name
                    DeathPunish.NOTHING -> DeathPunish.KICK.name
                }
            9 -> tool.toggleSetting(player, "Settings.Send Seed")
            10 -> tool.toggleSetting(player,"Settings.Send Location")

            //Goals
            101 -> tool.toggleSetting(player,"Goals.Dragon")
            102 -> tool.toggleSetting(player,"Goals.Wither")
            103 -> if (click.isLeftClick) tool.toggleSetting(player,"Goals.Player Death")
            else tool.toggleSetting(player, "Goals.Player Death Vanilla")
            104 -> tool.toggleSetting(player,"Goals.Elder Guardian")
            105 -> tool.toggleSetting(player,"Goals.Empty Server")
        }
        GUIBuilder(player, inv).scroll(0).open()
    }
}