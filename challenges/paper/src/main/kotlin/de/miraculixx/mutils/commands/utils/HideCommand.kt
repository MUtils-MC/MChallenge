@file:Suppress("UNCHECKED_CAST") // Collection casting

package de.miraculixx.mutils.commands.utils

import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mutils.PluginManager
import de.miraculixx.mutils.messages.msg
import de.miraculixx.mutils.messages.plus
import de.miraculixx.mutils.messages.prefix
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentManyPlayers
import dev.jorel.commandapi.kotlindsl.playerExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class HideCommand {
    @Suppress("unused")
    val hide = commandTree("hide", { sender: CommandSender -> sender.hasPermission("mutils.command.hide") }) {
        handleVisibility(true)
    }

    @Suppress("unused")
    val show = commandTree("show", { sender: CommandSender -> sender.hasPermission("mutils.command.hide") }) {
        handleVisibility(false)
    }

    private fun CommandTree.handleVisibility(hide: Boolean) {
        playerExecutor { player, _ ->
            onlinePlayers.forEach { target ->
                if (target == player) return@forEach
                if (hide) target.hidePlayer(PluginManager, player)
                else target.showPlayer(PluginManager, player)
            }
            player.sendMessage(prefix + msg("command.hide.${if (hide) "hide" else "show"}"))
        }
        entitySelectorArgumentManyPlayers("player") {
            anyExecutor { commandSender, args ->
                val sources = args[0] as Collection<Player>
                onlinePlayers.forEach { target ->
                    sources.forEach { source ->
                        if (source != target) {
                            if (hide) target.hidePlayer(PluginManager, source)
                            else target.showPlayer(PluginManager, source)
                        }
                    }
                }
                commandSender.sendMessage(prefix + msg("command.hide.${if (hide) "hide" else "show"}Multiple", listOf(sources.size.toString())))
            }
        }
    }
}