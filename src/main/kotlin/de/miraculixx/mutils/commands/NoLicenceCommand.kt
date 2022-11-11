package de.miraculixx.mutils.commands

import de.miraculixx.mutils.utils.prefix
import de.miraculixx.mutils.utils.tools.soundError
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class NoLicenceCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        sender.sendMessage("$prefix §cSorry, this Command is only available for Premium Users!")
        sender.sendMessage("$prefix Shop ->§b https://mutils.de/m/shop")
        sender.sendMessage("$prefix Support ->§b https://mutils.de/dc")
        if (sender is Player) sender.soundError()

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return if (args.size < 2)
            mutableListOf("<No Licence>")
        else mutableListOf()
    }
}