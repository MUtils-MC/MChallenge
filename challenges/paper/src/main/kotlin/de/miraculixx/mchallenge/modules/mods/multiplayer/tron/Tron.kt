package de.miraculixx.mchallenge.modules.mods.multiplayer.tron

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import org.bukkit.Material
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*

class Tron : Challenge {
    private val playerData: MutableMap<UUID, TronPath> = mutableMapOf()
    private val validColors = getColors()
    private var isInitial = true

    override fun register() {
        onJoin.register()
        if (isInitial) {
            isInitial = false
            return
        }
        playerData.forEach { (_, it) ->
            it.register()
        }
    }

    override fun unregister() {
        onJoin.unregister()
        playerData.forEach { (_, it) ->
            it.unregister()
        }
    }

    override fun start(): Boolean {
        onlinePlayers.forEach { player ->
//            if (Spectator.isSpectator(player.uniqueId)) return@forEach TODO
            if (validColors.isEmpty()) validColors.addAll(getColors())
            val color = validColors.random()
            validColors.remove(color)

            val uuid = player.uniqueId
            playerData[uuid] = TronPath(uuid, color)
        }
        return true
    }

    override fun stop() {
        playerData.forEach { (_, it) -> it.unregister() }
        playerData.clear()
    }

    private val onJoin = listen<PlayerJoinEvent> {
        val uuid = it.player.uniqueId
//        if (Spectator.isSpectator(uuid)) return@listen
        if (validColors.isEmpty()) validColors.addAll(getColors())
        val color = validColors.random()
        validColors.remove(color)
        if (!playerData.containsKey(uuid)) playerData[uuid] = TronPath(uuid, color)
    }


    private fun getColors(): MutableList<Material> {
        return Material.values().filter { it.name.endsWith("_CONCRETE") }.toMutableList()
    }
}