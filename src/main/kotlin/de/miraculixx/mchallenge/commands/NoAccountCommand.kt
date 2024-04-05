package de.miraculixx.mchallenge.commands

import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class NoAccountCommand : TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        sender.sendMessage(prefix + cmp("This command is only available with an connected MUtils account! Use /login to connect"))
        return true
    }
}