package de.miraculixx.mchallenge.modules.mods.boostUp

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import org.bukkit.entity.LivingEntity
import org.bukkit.event.player.PlayerMoveEvent

class BoostUp : Challenge {
    private val radius: Double
    private val boost: Int
    private val mode: Boolean

    init {
        val settings = challenges.getSetting(Challenges.BOOST_UP).settings
        radius = settings["radius"]?.toDouble()?.getValue() ?: 4.0
        boost = settings["boost"]?.toInt()?.getValue() ?: 5
        mode = settings["mode"]?.toBool()?.getValue() ?: true
    }

    override fun stop() {}

    override fun register() {
        onMove.register()
    }

    override fun unregister() {
        onMove.unregister()
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        val player = it.player
        player.getNearbyEntities(radius, radius, radius).forEach { entity ->
            if (entity is LivingEntity || mode)
                entity.velocity = entity.velocity.clone().setY(boost)
        }
    }
}