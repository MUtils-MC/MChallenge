package de.miraculixx.mutils.modules.creator

import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.utils.msg
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CreatorCommand: CommandExecutor {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if (p0 !is Player) {
            p0.sendMessage(msg("command.  notPlayer"))
            return false
        }

        GUIBuilder(p0, GUI.CREATOR_MAIN, GUIAnimation.SPLIT)
        return true
    }
}