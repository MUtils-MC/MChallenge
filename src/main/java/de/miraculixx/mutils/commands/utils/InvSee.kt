package de.miraculixx.mutils.commands.utils

import de.miraculixx.mutils.utils.msg
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class InvSee : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(msg("command.noPlayer"))
            return false
        }
        if (p3.isEmpty()) {
            sender.sendMessage(msg("command.invsee.help", pre = false))
            return false
        }
        val player = Bukkit.getPlayer(p3[0])
        if (player == null || !player.isOnline) {
            sender.sendMessage(msg("command.notOnline"))
            return false
        }
        sender.openInventory(player.inventory)
        sender.playSound(sender.location, Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f)
        return true
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        if (p3.size < 2) onlinePlayers.forEach { list.add(it.name) }
        return list
    }
}