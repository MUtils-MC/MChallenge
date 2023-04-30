package de.miraculixx.mchallenge.modules.mods.randomizer

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.challenge.api.modules.challenges.Challenges
import de.miraculixx.challenge.api.settings.challenges
import de.miraculixx.challenge.api.settings.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.mcore.utils.getLivingMobs
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import kotlin.random.Random
import kotlin.random.nextInt

class EntityDamageRandomizer : Challenge {
    private var random = true
    private val map: MutableMap<EntityType, Int> = mutableMapOf()

    init {
        val settings = challenges.getSetting(Challenges.RANDOMIZER_DAMAGE).settings
        random = settings["random"]?.toBool()?.getValue() ?: true
    }

    override fun start(): Boolean {
        val rnd = Random(worlds.first().seed)
        val types = getLivingMobs(true).shuffled(rnd)
        if (!random) {
            map.clear()
            types.forEach { type ->
                map[type] = Random.nextInt(1..19)
            }
        }
        return true
    }

    override fun stop() {
        map.clear()
    }

    override fun register() {
        onDamage.register()
    }

    override fun unregister() {
        onDamage.unregister()
    }

    private val onDamage = listen<EntityDamageByEntityEvent>(register = false) {
        val target = it.entity
        if (target !is Player) return@listen
        val damager = it.damager
        val type = if (damager is LivingEntity) damager.type else {
            if (damager is Projectile) {
                val shooter = damager.shooter
                if (shooter is LivingEntity) shooter.type else return@listen
            } else return@listen
        }
        it.damage = if (random) {
            Random.nextInt(1..19).toDouble()
        } else {
            map[type]?.toDouble() ?: 2.0
        }
    }
}