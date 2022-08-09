package de.miraculixx.mutils.modules.spectator

import de.miraculixx.mutils.commands.tools.CommandTools
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.utils.msg
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class SpecCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage(msg("command.spectator.help", pre = false))
            return false
        }

        val tools = CommandTools()
        val list = if (args.size >= 2 && args[1].startsWith('@')) tools.selector(sender, args[1])
        else if (args.size >= 2) {
            val target = Bukkit.getPlayer(args[1])
            if (target == null) {
                sender.sendMessage(msg("command.notOnline"))
                return false
            }
            List<Entity>(1) { target }
        } else List<Entity>(1) {
            if (sender !is Player) {
                sender.sendMessage(msg("command.notPlayer"))
                return false
            }
            sender
        }

        list.forEach {
            if (sender is Player) {
                it as Player
                when (args[0]) {
                    "join" -> {
                        if (Spectator.isSpectator(it.uniqueId))
                            it.sendMessage(msg("command.spectator.isSpectator", it))
                        else {
                            Spectator.setSpectator(it)
                            it.sendMessage(msg("command.spectator.join"))
                        }
                    }
                    "leave" -> {
                        if (!Spectator.isSpectator(it.uniqueId))
                            it.sendMessage(msg("command.spectator.notSpectator", it))
                        else {
                            Spectator.unsetSpectator(it)
                            it.sendMessage(msg("command.spectator.leave"))
                        }
                    }
                    "items" -> {
                        if (!Spectator.isSpectator(it.uniqueId))
                            it.sendMessage(msg("command.spectator.notSpectator", it))
                        else GUIBuilder(sender, GUI.SPEC_HOTBAR).player()
                    }
                    else -> it.sendMessage(msg("command.spectator.help", pre = false))
                }
            }
        }
        return true
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        list.add("join")
        list.add("leave")
        list.add("items")

        return list
    }
}