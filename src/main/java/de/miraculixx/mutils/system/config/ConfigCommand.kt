package de.miraculixx.mutils.system.config

import de.miraculixx.mutils.utils.text.addLines
import de.miraculixx.mutils.utils.text.msg
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import java.util.*

class ConfigCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage(msg("command.config.help", pre = false))
            return false
        }

        val s = args[1]
        val target = if (s.lowercase(Locale.getDefault()) == "all")
            Configs.values().toList()
        else try {
            listOf(Configs.valueOf(s))
        } catch (e: IllegalArgumentException) {
            sender.sendMessage(msg("command.config.help", pre = false))
            return false
        }
        val s2 = args[0].lowercase(Locale.getDefault())
        when (s2) {
            "save" -> target.forEach {
                ConfigManager.save(it)
            }
            "reload" -> target.forEach {
                ConfigManager.reload(it)
            }
            "reset" -> target.forEach {
                ConfigManager.reset(it)
            }
            else -> {
                sender.sendMessage(msg("command.config.help", pre = false))
                return false
            }
        }
        sender.sendMessage(msg("command.config.$s2"))
        return true
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, args: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        if (args.size < 2)
            list.addLines("save", "reload", "reset")
        else if (args.size == 2) {
            Configs.values().forEach {
                list.add(it.name)
            }
            list.add("all")
        }
        return list
    }
}