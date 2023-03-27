package de.miraculixx.mutils.modules.mods.damageDuel

import de.miraculixx.api.modules.challenges.Challenge
import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.api.settings.challenges
import de.miraculixx.api.settings.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mutils.modules.spectator.Spectator
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent

class DamageDuell : Challenge {
    override val challenge = Challenges.DAMAGE_DUELL
    private val percentage: Int

    init {
        val settings = challenges.getSetting(challenge).settings
        percentage = settings["percent"]?.toInt()?.getValue() ?: 50
    }

    override fun start(): Boolean {
        return true
    }

    override fun register() {
        onMelee.register()
    }

    override fun unregister() {
        onMelee.unregister()
    }

    private val onMelee = listen<EntityDamageByEntityEvent>(register = false) {
        if (it.damager !is Player && it.damager !is Projectile) return@listen
        if (it.damager is Projectile) {
            val shooter = (it.damager as Projectile).shooter ?: return@listen
            if (shooter !is Player) return@listen
        }
        val damage = it.finalDamage * (percentage / 100.0)
        onlinePlayers.forEach { player ->
            if (!Spectator.isSpectator(player.uniqueId)) {
                if (player.health < 2) player.health += 0.01
                player.damage(0.01)
                if (player.health - damage >= 1) player.health -= damage
                else player.health = 1.0
            }
        }
    }
}