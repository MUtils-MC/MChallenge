package de.miraculixx.mchallenge.commands.utils

import de.miraculixx.kpaper.extensions.bukkit.msg
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.utils.config.ConfigManager
import de.miraculixx.timer.api.MTimerAPI
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerKickEvent
import kotlin.time.Duration

class ResetCommand {

    @Suppress("unused")
    private val reset = commandTree("reset") {
        withPermission("mutils.reset")
        anyExecutor { sender, _ ->
            BackpackCommand.reset()
            PositionCommand.reset()

            ConfigManager.settings.reset = true
            ConfigManager.settings.worlds.addAll(worlds.map { it.name })
            onlinePlayers.forEach { player -> player.kick(sender.msg("command.reset", listOf(sender.name)), PlayerKickEvent.Cause.RESTART_COMMAND) }
            if (Bukkit.getPluginManager().isPluginEnabled("MUtils-Timer")) MTimerAPI.INSTANCE?.setTime(Duration.ZERO)
            ChallengeManager.reset()
            Bukkit.spigot().restart()
        }
    }
}