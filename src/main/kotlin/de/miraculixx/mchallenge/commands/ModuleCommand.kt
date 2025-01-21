package de.miraculixx.mchallenge.commands

import de.miraculixx.kpaper.extensions.bukkit.msg
import de.miraculixx.kpaper.extensions.bukkit.register
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class ModuleCommand(name: String) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage(prefix + sender.msg("command.notRegistered"))
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }

    init {
        register(name)
    }
}