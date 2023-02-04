package de.miraculixx.mutils.modules.mods.vampire

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mutils.enums.Challenges
import de.miraculixx.mutils.modules.Challenge
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class Vampire : Challenge {
    override val challenge = Challenges.VAMPIRE
    private val dataMap: MutableMap<UUID, VampireData> = mutableMapOf()

    override fun register() {
        onJoin.register()
        onQuit.register()
    }

    override fun unregister() {
        onJoin.unregister()
        onQuit.unregister()
    }

    override fun start(): Boolean {
        onlinePlayers.forEach { player ->
//            if (Spectator.isSpectator(player.uniqueId)) return@forEach
            dataMap[player.uniqueId] = VampireData(player.uniqueId)
        }
        return true
    }

    override fun stop() {
        dataMap.forEach { (_, data) ->
            data.stop()
        }
        dataMap.clear()
    }

    private val onJoin = listen<PlayerJoinEvent>(priority = EventPriority.HIGHEST) {
        val player = it.player
        //if (Spectator.isSpectator(player.uniqueId)) return@listen
        dataMap.getOrPut(player.uniqueId) { VampireData(player.uniqueId) }.running = true
    }

    private val onQuit = listen<PlayerQuitEvent> {
        dataMap[it.player.uniqueId]?.running = false
    }
}