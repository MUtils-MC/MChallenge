package de.miraculixx.mutils.modules.mods.rocket

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.api.modules.challenges.Challenge
import de.miraculixx.mutils.modules.spectator.Spectator
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import java.util.UUID

class Rocket: Challenge {
    override val challenge = Challenges.ROCKET
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