@file:Suppress("UNCHECKED_CAST") // Collection casting

package de.miraculixx.mchallenge.commands.utils

import de.miraculixx.kpaper.extensions.bukkit.feed
import de.miraculixx.kpaper.extensions.bukkit.heal
import de.miraculixx.kpaper.extensions.bukkit.msg
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix
import dev.jorel.commandapi.kotlindsl.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

class HealCommand {
    @Suppress("unused")
    val heal = commandTree("heal") {
        withPermission("command.heal")
        playerExecutor { player, _ ->
            player.sendMessage(prefix + player.msg("command.heal.single"))
            player.heal()
            player.feed()
        }
        entitySelectorArgumentManyPlayers("player") {
            anyExecutor { sender, args ->
                val targets = args[0] as Collection<Player>
                sender.sendMessage(prefix + sender.msg("command.heal.multiple", listOf(targets.size.toString())))
                targets.forEach { p ->
                    p.heal()
                    p.feed()
                }
            }
            booleanArgument("attribute") {
                anyExecutor { sender, args ->
                    val targets = args[0] as Collection<Player>
                    sender.sendMessage(prefix + sender.msg("command.heal.multiple", listOf(targets.size.toString())))
                    targets.forEach { p ->
                        p.heal()
                        p.feed()
                        p.isFlying = false
                        p.setGravity(true)
                        p.flySpeed = 0.1f
                        p.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 20.0
                        p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.baseValue = 0.1
                        p.clearActivePotionEffects()
                    }
                }
            }
        }
    }
}