package de.miraculixx.mutils.commands.tools

import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity

class CommandTools {

    fun selector(sender: CommandSender, selector: String): List<Entity> {
        val targets = ArrayList<Entity>()
        when (selector) {
            "@a" -> {
                for (onlinePlayer in onlinePlayers) {
                    targets.add(onlinePlayer)
                }
            }
            "@r" -> targets.add(onlinePlayers.random())
            "@s" -> {
                if (sender is Entity) {
                    targets.add(sender)
                }
            }
            "@e" -> {
                for (onlinePlayer in onlinePlayers) {
                    for (entity in onlinePlayer.world.entities) {
                        if (!targets.contains(entity)) targets.add(entity)
                    }
                }
            }
        }
        return targets
    }
}