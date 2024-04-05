package de.miraculixx.mchallenge.modules.mods.simple.stayAway

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mcommons.namespace
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.persistence.PersistentDataType

class StayAway : Challenge {
    private val distance: Double
    private val warningDamage: Boolean

    init {
        val settings = challenges.getSetting(Challenges.STAY_AWAY).settings
        distance = settings["distance"]?.toDouble()?.getValue() ?: 3.0
        warningDamage = settings["warning"]?.toBool()?.getValue() ?: true
    }

    override fun register() {
        onMove.register()
    }

    override fun unregister() {
        onMove.unregister()
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        val player = it.player
        val gm = player.gameMode
        if (gm == GameMode.CREATIVE || gm == GameMode.SPECTATOR) return@listen
        if (Spectator.isSpectator(player.uniqueId)) return@listen
        player.getNearbyEntities(distance + 2, distance + 2, distance + 2).forEach { e ->
            if (e is LivingEntity) {
                if (e is Player && (Spectator.isSpectator(e.uniqueId) || e.gameMode != GameMode.SURVIVAL)) return@forEach
                if (e.location.distance(player.location) <= distance) {
                    player.persistentDataContainer.set(NamespacedKey(namespace, "death.custom"), PersistentDataType.STRING, "stayAway")
                    player.damage(999.0)
                    e.isGlowing = true
                } else if (warningDamage) player.damage(0.01)
                return@listen
            }
        }
    }
}