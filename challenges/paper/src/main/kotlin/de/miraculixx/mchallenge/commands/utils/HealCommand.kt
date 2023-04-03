@file:Suppress("UNCHECKED_CAST") // Collection casting

package de.miraculixx.mchallenge.commands.utils

import de.miraculixx.kpaper.extensions.bukkit.feedSaturate
import de.miraculixx.kpaper.extensions.bukkit.heal
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentManyPlayers
import dev.jorel.commandapi.kotlindsl.playerExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class HealCommand {
    @Suppress("unused")
    val heal = commandTree("heal", { sender: CommandSender -> sender.hasPermission("mutils.command.heal") }) {
        playerExecutor { player, _ ->
            player.sendMessage(prefix + msg("command.heal.single"))
            player.heal()
            player.feedSaturate()
        }
        entitySelectorArgumentManyPlayers("player") {
            anyExecutor { commandSender, args ->
                val targets = args[0] as Collection<Player>
                commandSender.sendMessage(prefix + msg("command.heal.multiple", listOf(targets.size.toString())))
                targets.forEach { p ->
                    p.heal()
                    p.feedSaturate()
                }
            }
        }
    }
}