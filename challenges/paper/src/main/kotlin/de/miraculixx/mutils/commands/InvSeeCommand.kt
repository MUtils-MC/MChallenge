package de.miraculixx.mutils.commands

import de.miraculixx.kpaper.extensions.bukkit.register
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class InvSeeCommand(name: String): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val target = args?.get(0) ?: return false
        val targetPlayer = Bukkit.getPlayer(target) ?: return false
        (sender as Player).openInventory(targetPlayer.inventory)
        return true
    }

    init {
        register(name)
    }
}