package de.miraculixx.mutils.commands.utils

import de.miraculixx.mutils.commands.tools.CommandTools
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Entity
import org.bukkit.entity.Player


class GamemodeCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (args.size != 1 && args.size != 2) {
            sender.sendMessage(msg("command.gamemode.help", pre = false))
            return false
        }

        val gameMode: GameMode? =
        when (args[0]) {
            "survival", "0" -> GameMode.SURVIVAL
            "creative", "1" -> GameMode.CREATIVE
            "adventure", "2" -> GameMode.ADVENTURE
            "spectator", "3" -> GameMode.SPECTATOR
            else -> null
        }
        if (gameMode == null) {
            sender.sendMessage(msg("command.gamemode.help", pre = false))
            return false
        }

        if (args.size == 1) {
            if (sender !is Player) {
                sender.sendMessage(msg("command.notPlayer"))
                return false
            }
            sender.gameMode = gameMode
            return true
        } else {
            val cmdTools = CommandTools()
            val targets = ArrayList<Entity>()
            if (args[1].startsWith("@")) {
                cmdTools.selector(sender, args[1]).forEach {
                    targets.add(it)
                }
            } else {
                val target = Bukkit.getPlayer(args[1])
                if (target == null || !target.isOnline) {
                    sender.sendMessage(msg("command.notPlayer"))
                    return false
                }
                targets.add(target)
            }

            for (target in targets) {
                if (target !is Player) continue
                target.gameMode = gameMode
                sender.sendMessage(msg("command.gamemode.change",target,gameMode.name))
            }
            return true
        }
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        if (p3.isEmpty() || p3.size == 1) {
            list.add("survival")
            list.add("creative")
            list.add("adventure")
            list.add("spectator")
        } else if (p3.size == 2) {
            onlinePlayers.forEach { player ->
                list.add(player.name)
            }
            list.add("@a")
            list.add("@r")
            list.add("@s")
        }

        return list
    }
}