package de.miraculixx.mutils.modules.mods.vampire

import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mutils.modules.challenges.AbstractPaperChallenge
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class Vampire : AbstractPaperChallenge() {
    override val challenge = Challenges.VAMPIRE
    private val dataMap: MutableMap<UUID, VampireData> = mutableMapOf()

    init {
        onEvent<PlayerJoinEvent>(priority = EventPriority.HIGHEST) {
            val player = it.player
            //if (Spectator.isSpectator(player.uniqueId)) return@listen
            dataMap.getOrPut(player.uniqueId) { VampireData(player.uniqueId) }.running = true
        }
        onEvent<PlayerQuitEvent> {
            dataMap[it.player.uniqueId]?.running = false
        }
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
}
