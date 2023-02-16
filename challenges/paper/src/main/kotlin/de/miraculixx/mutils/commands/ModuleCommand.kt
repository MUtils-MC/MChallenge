package de.miraculixx.mutils.commands

import de.miraculixx.kpaper.extensions.bukkit.register
import de.miraculixx.mutils.messages.msg
import de.miraculixx.mutils.messages.plus
import de.miraculixx.mutils.messages.prefix
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class ModuleCommand(name: String) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        sender.sendMessage(prefix + msg("command.notRegistered"))
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String> {
        return mutableListOf()
    }

    init {
        register(name)
    }
}