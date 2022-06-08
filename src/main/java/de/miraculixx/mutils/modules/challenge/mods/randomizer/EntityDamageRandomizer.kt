package de.miraculixx.mutils.modules.challenge.mods.randomizer

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.worlds
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

class EntityDamageRandomizer : Challenge() {
    override val challenge = Modules.RANDOMIZER_ENTITY_DAMAGE
    private var random = true
    private val map = HashMap<EntityType, Int>()

    override fun start(): Boolean {
        val c = ConfigManager.getConfig(Configs.MODULES)
        val rnd = Random(worlds.first().seed)
        random = c.getBoolean("RANDOMIZER_ENTITY_DAMAGE.Random")
        val types = kotlin.collections.ArrayList<EntityType>(Arrays.stream(EntityType.values()).filter { type ->
            type.entityClass != null && LivingEntity::class.java.isAssignableFrom(type.entityClass) && type != EntityType.PLAYER
        }.toList()).shuffled(rnd)
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