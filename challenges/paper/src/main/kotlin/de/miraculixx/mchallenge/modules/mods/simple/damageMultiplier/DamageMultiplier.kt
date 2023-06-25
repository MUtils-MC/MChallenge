package de.miraculixx.mchallenge.modules.mods.simple.damageMultiplier

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import net.minecraft.world.entity.player.Player
import org.bukkit.event.entity.EntityDamageEvent

class DamageMultiplier: Challenge {
    private val multiplier: Double

    init {
        val settings = challenges.getSetting(Challenges.DAMAGE_MULTIPLIER).settings
        multiplier = settings["multiplier"]?.toDouble()?.getValue() ?: 2.0
    }

    override fun register() {
        onDamage.register()
    }

    override fun unregister() {
        onDamage.unregister()
    }

    private val onDamage = listen<EntityDamageEvent>(register = false) {
        if (it.entity !is Player) return@listen
        it.damage = it.finalDamage * multiplier
    }
}