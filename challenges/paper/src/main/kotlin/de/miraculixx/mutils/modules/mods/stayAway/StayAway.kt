package de.miraculixx.mutils.modules.mods.stayAway

import de.miraculixx.api.modules.challenges.Challenge
import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.api.settings.challenges
import de.miraculixx.api.settings.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.mutils.messages.namespace
import de.miraculixx.mutils.modules.spectator.Spectator
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.persistence.PersistentDataType

class StayAway : Challenge {
    override val challenge = Challenges.STAY_AWAY
    private val distance: Double
    private val warningDamage: Boolean

    init {
        val settings = challenges.getSetting(challenge).settings
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
                if (e is Player && Spectator.isSpectator(e.uniqueId)) return@forEach
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