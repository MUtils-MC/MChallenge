package de.miraculixx.mutils.commands.utils

import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.text.msg
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class LanguageCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val c = ConfigManager.getConfig(Configs.SETTINGS)
        val current = c.getString("Language")
        if (args.isEmpty()) {
            sender.sendMessage(msg("command.lang.current", input = current))
            return true
        }
        when (args[0].lowercase()) {
            "get" -> sender.sendMessage(msg("command.lang.current", input = current))
            "set" -> {
                if (args.size < 2) {
                    sender.sendMessage(msg("command.lang.help", pre = false))
                    return false
                }
                c["Language"] = args[1]
                ConfigManager.updateLang()
                sender.sendMessage(msg("command.lang.switch", input = args[1], input2 = current))
            }
            "reload" -> {
                ConfigManager.updateLang()
                sender.sendMessage(msg("command.lang.update", input = current))
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        if (args.size < 2) {
            list.add("get")
            list.add("set")
            list.add("reload")
        } else if (args.size == 2 && args[0] == "set") {
            list.add("custom")
            list.add("EN_US")
            list.add("DE_DE")
        }
        return list
    }
}