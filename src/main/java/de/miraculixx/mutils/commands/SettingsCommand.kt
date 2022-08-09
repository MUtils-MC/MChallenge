package de.miraculixx.mutils.commands

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.utils.msg
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*

class SettingsCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(msg("command.notPlayer"))
            return false
        }
        if (ModuleManager.isActive(Modules.SPEEDRUN)) {
            sender.sendMessage(msg("command.speedrun.notify"))
            return false
        }
        if (args.isEmpty()) {
            GUIBuilder(sender,GUI.SELECT_MENU, animation = GUIAnimation.WATERFALL_OPEN).custom().open()
            return true
        }
        when (args[0].lowercase(Locale.getDefault())) {
            "challenge" -> GUIBuilder(sender, GUI.CHALLENGE, animation = GUIAnimation.WATERFALL_OPEN).scroll(0).open()
            "timer" -> GUIBuilder(sender, GUI.TIMER_SETTINGS, animation = GUIAnimation.WATERFALL_OPEN).custom().open()
        }
        return true
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): MutableList<String>? {
        val list = ArrayList<String>()
        if (p3.size < 2) {
            list.add("challenge")
            list.add("timer")
        }
        return list
    }
}