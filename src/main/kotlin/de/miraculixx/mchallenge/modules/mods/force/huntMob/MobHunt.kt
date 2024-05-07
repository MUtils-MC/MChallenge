package de.miraculixx.mchallenge.modules.mods.force.huntMob

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.items.getLivingMobs
import de.miraculixx.mchallenge.modules.challenges.interfaces.HuntChallenge
import de.miraculixx.mchallenge.utils.serializer.Serializer
import de.miraculixx.mcommons.extensions.enumOf
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent

class MobHunt : Challenge, HuntChallenge<EntityType>("mobhunt", "mob_hunt") {
    override val typeName = "Mob"
    override var currentTarget: EntityType? = null
    override val allEntries = getLivingMobs(true)
    override val maxEntries = allEntries.size
    override val remainingEntries = mutableListOf<EntityType>()
    override val serializer: Serializer<EntityType> = object: Serializer<EntityType> {
        override fun toString(data: EntityType) = data.name
        override fun toObject(data: String) = enumOf<EntityType>(data) ?: EntityType.ZOMBIE
    }

    override fun register() {
        onKill.register()
    }

    override fun unregister() {
        stop()
        onKill.unregister()
    }

    override fun start(): Boolean {
        startHunt()
        return true
    }

    override fun stop() {
        stopHunt()
    }

    override fun getTranslationKey() = currentTarget?.translationKey()?.let { "<lang:$it>" }

    private val onKill = listen<EntityDamageByEntityEvent>(register = false) {
        val target = it.entity
        if (target !is LivingEntity) return@listen
        val player = when (val damager = it.damager) {
            is Player -> damager
            is Projectile -> damager.shooter as? Player ?: return@listen
            else -> return@listen
        }

        if (target.health - it.finalDamage > 0.0) return@listen
        val type = target.type
        if (type != currentTarget) return@listen

        nextEntry(player.name, player)
    }
}