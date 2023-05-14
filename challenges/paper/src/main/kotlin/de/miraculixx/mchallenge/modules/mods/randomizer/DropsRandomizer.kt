package de.miraculixx.mchallenge.modules.mods.randomizer

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix
import de.miraculixx.mcore.utils.getLivingMobs
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.loot.LootContext
import org.bukkit.loot.LootTables
import kotlin.random.Random

class DropsRandomizer : Challenge {
    private val random: Boolean
    private val map: MutableMap<EntityType, EntityType> = mutableMapOf()
    private val list: MutableList<EntityType> = mutableListOf()
    private val announced: MutableSet<EntityType> = mutableSetOf()

    init {
        val settings = challenges.getSetting(Challenges.RANDOMIZER_ENTITY).settings
        random = settings["random"]?.toBool()?.getValue() ?: true
    }

    override fun start(): Boolean {
        val rnd = Random(worlds.first().seed)
        val l = getLivingMobs(false).shuffled(rnd)
        if (!random) {
            val drops = l.toMutableList()
            drops.shuffle(rnd)
            l.forEach { kill ->
                val type = drops.random(rnd)
                map[kill] = type
                drops.remove(type)
            }
        } else list.addAll(l)
        return true
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
        val eType = entity.type
        val targetType = if (random) {
            list[Random.nextInt(0, list.size - 1)]
        } else map[eType] ?: EntityType.ZOMBIE

        val loc = entity.location
        val context = LootContext.Builder(loc).killer(entity.killer).lootedEntity(entity).lootingModifier(2).build()
        val tableName = targetType.name
        val lootTable = LootTables.valueOf(tableName).lootTable
        if (announced.contains(eType)) {
            announced.add(eType)
            broadcast(prefix + cmp("${eType.name} >> ${targetType.name}"))
        }
        lootTable.populateLoot(java.util.Random(loc.world.seed), context).forEach { item ->
            loc.world.dropItem(loc, item)
        }
    }
}