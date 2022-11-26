package de.miraculixx.mutils.modules.challenge.mods.randomizer

import de.miraculixx.mutils.challenge.utils.enums.Challenge
import de.miraculixx.mutils.challenge.modules.Challenge
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.worlds
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.loot.LootContext
import org.bukkit.loot.LootTables
import java.util.*
import kotlin.random.Random

class DropsRandomizer : Challenge {
    override val challenge = Challenge.RANDOMIZER_ENTITY
    private var random = false
    private val map = HashMap<EntityType, EntityType>()
    private var list: List<EntityType>? = null

    override fun start(): Boolean {
        val rnd = Random(worlds.first().seed)
        val c = ConfigManager.getConfig(Configs.MODULES)
        random = c.getBoolean("RANDOMIZER_ENTITY.Random")
        val l = kotlin.collections.ArrayList<EntityType>(Arrays.stream(EntityType.values()).filter {
                entityType -> entityType.entityClass != null && LivingEntity::class.java.isAssignableFrom(entityType.entityClass) && entityType != EntityType.PLAYER
        }.toList()).shuffled(rnd)
        if (!random) {
            val drops = l.toMutableList()
            drops.shuffle(rnd)
            l.forEach { kill ->
                val type = drops.random(rnd)
                map[kill] = type
                drops.remove(type)
            }
        } else list = l
        return true
    }

    override fun stop() {
        list = null
        map.clear()
    }

    override fun register() {
        onKill.register()
    }

    override fun unregister() {
        onKill.unregister()
    }

    private val onKill = listen<EntityDeathEvent>(register = false) {
        it.drops.clear()
        val entity = it.entity
        val targetType = if (random) {
            if (list == null) return@listen
            list!![Random.nextInt(0, list!!.size - 1)]
        } else map[entity.type] ?: EntityType.ZOMBIE

        val loc = entity.location
        val context = LootContext.Builder(loc).killer(entity.killer).lootedEntity(entity).lootingModifier(1).build()
        val tableName = when (targetType) {
            EntityType.MUSHROOM_COW -> "COW"
            else -> targetType.name
        }
        val drops = LootTables.valueOf(tableName).lootTable.populateLoot(java.util.Random(loc.world.seed), context)
        drops.forEach { item ->
            loc.world.dropItem(loc, item)
        }
    }
}