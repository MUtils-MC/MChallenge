package de.miraculixx.mutils.modules.gui

import de.miraculixx.mutils.enums.modules.timer.DeathPunish
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIState
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.InvUtils
import de.miraculixx.mutils.utils.tools.click
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
                InvUtils.navigate(player, change, GUI.TIMER_SETTINGS, GUIState.SCROLL)
                return
            }
            203 -> {
                val change = if (click.isShiftClick) 5
                else 1
                InvUtils.navigate(player, change, GUI.TIMER_SETTINGS, GUIState.SCROLL)
                return
            }

            6 -> InvUtils.toggleSetting(c, player,"Settings.Spec on Death")
            7 -> InvUtils.toggleSetting(c, player,"Settings.Spec on Join")
            8 -> if (click.isLeftClick) InvUtils.toggleSetting(c, player, "Settings.Punishment")
                else c["Settings.Death Punishment"] =
                when (DeathPunish.valueOf(c.getString("Settings.Death Punishment")?:"NOTHING")) {
                    DeathPunish.KICK -> DeathPunish.BAN.name
                    DeathPunish.BAN -> DeathPunish.NOTHING.name
                    DeathPunish.NOTHING -> DeathPunish.KICK.name
                }
            9 -> InvUtils.toggleSetting(c, player, "Settings.Send Seed")
            10 -> InvUtils.toggleSetting(c, player,"Settings.Send Location")

            //Goals
            101 -> InvUtils.toggleSetting(c, player,"Goals.Dragon")
            102 -> InvUtils.toggleSetting(c, player,"Goals.Wither")
            103 -> if (click.isLeftClick) InvUtils.toggleSetting(c, player,"Goals.Player Death")
            else InvUtils.toggleSetting(c, player, "Goals.Player Death Vanilla")
            104 -> InvUtils.toggleSetting(c, player,"Goals.Elder Guardian")
            105 -> InvUtils.toggleSetting(c, player,"Goals.Empty Server")
        }
        GUIBuilder(player, inv).scroll(0).open()
    }
}