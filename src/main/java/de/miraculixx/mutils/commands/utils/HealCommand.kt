package de.miraculixx.mutils.commands.utils

import de.miraculixx.mutils.commands.tools.CommandTools
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.extensions.bukkit.feed
import net.axay.kspigot.extensions.bukkit.feedSaturate
import net.axay.kspigot.extensions.bukkit.heal
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class HealCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(msg("command.noPlayer"))
            return false
        }

        val targets = ArrayList<Entity>()
        if (args.isEmpty()) {
            targets.add(sender)
        } else {
            if (args[0].startsWith("@")) {
                CommandTools().selector(sender, args[0]).forEach {
                    targets.add(it)
                }
            } else {
                val target = Bukkit.getPlayer(args[0])
                if (target == null || !target.isOnline) {
                    sender.sendMessage(msg("command.heal.help", pre = false))
                    return false
                }
                targets.add(target)
            }
        }

        for (entity in targets) {
            if (entity !is LivingEntity) continue
            entity.heal()
            if (entity !is Player) continue
            entity.feed()
            entity.feedSaturate()
        }
        if (args.contains("@")) sender.sendMessage(msg("command.heal.multiple", input = targets.size.toString()))
        else sender.sendMessage(msg("command.heal.heal"))
        return true
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()
        if (p3.size <= 1) {
            list.add("@a")
            list.add("@e")
            list.add("@r")
            list.add("@s")
            onlinePlayers.forEach { list.add(it.name) }
        }
        return list
    }
}