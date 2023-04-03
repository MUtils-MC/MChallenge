package de.miraculixx.mchallenge.commands

import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class NoAccountCommand: TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (de.miraculixx.mchallenge.MChallenge.bridgeAPI != null) sender.sendMessage(prefix + cmp("This command is only available with an connected MUtils account! Use /login to connect"))
        else sender.sendMessage(prefix + cmp("This command is only available with an connected MUtils account! Install MBridge to connect one (/ch bridge-install)"))
        return true
    }
}