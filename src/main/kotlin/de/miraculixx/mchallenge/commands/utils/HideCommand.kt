@file:Suppress("UNCHECKED_CAST") // Collection casting

package de.miraculixx.mchallenge.commands.utils

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.extensions.bukkit.msg
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.PluginManager
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentManyPlayers
import dev.jorel.commandapi.kotlindsl.playerExecutor
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent

class HideCommand {
    private val hiddenUsers = mutableSetOf<Player>()
    private var initialized = false

    @Suppress("unused")
    val hide = commandTree("hide") {
        withPermission("mutils.hide")
        handleVisibility(true)
    }

    @Suppress("unused")
    val show = commandTree("show") {
        withPermission("mutils.hide")
        handleVisibility(false)
    }

    private fun CommandTree.handleVisibility(hide: Boolean) {
        playerExecutor { player, _ ->
            if (hide) hiddenUsers.add(player)
            else hiddenUsers.remove(player)

            initializing()
            onlinePlayers.forEach { target ->
                if (target == player) return@forEach
                if (hide) target.hidePlayer(PluginManager, player)
                else target.showPlayer(PluginManager, player)
            }
            player.sendMessage(prefix + player.msg("command.hide.${if (hide) "hide" else "show"}"))
        }
        entitySelectorArgumentManyPlayers("player") {
            anyExecutor { sender, args ->
                val sources = args[0] as Collection<Player>
                if (hide) hiddenUsers.addAll(sources)
                else hiddenUsers.removeAll(sources)

                initializing()
                onlinePlayers.forEach { target ->
                    sources.forEach { source ->
                        if (source != target) {
                            if (hide) target.hidePlayer(PluginManager, source)
                            else target.showPlayer(PluginManager, source)
                        }
                    }
                }
                sender.sendMessage(prefix + sender.msg("command.hide.${if (hide) "hide" else "show"}Multiple", listOf(sources.size.toString())))
            }
        }
    }

    private fun initializing() {
        if (initialized) return
        initialized = true
        listen<PlayerJoinEvent> {
            hiddenUsers.forEach { it.hidePlayer(PluginManager, it) }
            if (it.player in hiddenUsers) {
                onlinePlayers.forEach { target ->
                    if (target == it.player) return@forEach
                    target.hidePlayer(PluginManager, it.player)
                }
            }
        }
    }
}