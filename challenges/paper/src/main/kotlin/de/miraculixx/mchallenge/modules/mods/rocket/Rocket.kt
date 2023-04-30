package de.miraculixx.mchallenge.modules.mods.rocket

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.mchallenge.modules.spectator.Spectator
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import java.util.*

class Rocket : Challenge {
    private val playerData: MutableMap<UUID, RocketPlayerData> = mutableMapOf()

    override fun register() {
        onToggleSneak.register()
        onJoin.register()
        onQuit.register()
    }

    override fun unregister() {
        onToggleSneak.unregister()
        onJoin.unregister()
        onQuit.unregister()
    }

    private val onToggleSneak = listen<PlayerToggleSneakEvent>(register = false) {
        val player = it.player
        val uuid = player.uniqueId
        if (Spectator.isSpectator(uuid)) return@listen
        if (it.isSneaking) playerData[uuid]
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        val player = it.player
        val uuid = player.uniqueId
        if (Spectator.isSpectator(uuid)) return@listen
        playerData[uuid] = RocketPlayerData(player)
    }

    private val onQuit = listen<PlayerQuitEvent>(register = false) {
        val player = it.player
        val uuid = player.uniqueId
        val data = playerData.remove(uuid) ?: return@listen
        data.stopBoost()
        data.stop()
    }
}