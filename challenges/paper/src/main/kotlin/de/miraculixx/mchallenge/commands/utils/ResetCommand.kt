package de.miraculixx.mchallenge.commands.utils

import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mvanilla.messages.msg
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.player.PlayerKickEvent

class ResetCommand {
    @Suppress("unused")
    val reset = commandTree("reset", { sender: CommandSender -> sender.hasPermission("mutils.reset") }) {
        anyExecutor { commandSender, _ ->
            MChallenge.settings.reset = true
            MChallenge.settings.worlds.addAll(worlds.map { it.name })
            onlinePlayers.forEach { player -> player.kick(msg("command.reset", listOf(commandSender.name)), PlayerKickEvent.Cause.RESTART_COMMAND) }
            Bukkit.spigot().restart()
        }
    }
}