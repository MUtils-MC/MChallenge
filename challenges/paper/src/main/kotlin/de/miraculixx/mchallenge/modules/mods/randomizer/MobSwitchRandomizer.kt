package de.miraculixx.mchallenge.modules.mods.randomizer

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.runnables.async
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.mcore.utils.getLivingMobs
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.CreatureSpawnEvent
import kotlin.random.Random

class MobSwitchRandomizer : Challenge {
    private val random: Boolean
    private var list: MutableList<EntityType> = mutableListOf()
    private val map: MutableMap<EntityType, EntityType> = mutableMapOf()

    init {
        val settings = challenges.getSetting(Challenges.RANDOMIZER_MOBS).settings
        random = settings["random"]?.toBool()?.getValue() ?: false
    }

    override fun start(): Boolean {
        val rnd = Random(worlds.first().seed)
        val key = getLivingMobs(false).shuffled(rnd)

        if (random) list.addAll(key)
        else {
            val target = key.toList().shuffled(rnd)
            var counter = 0
            target.forEach { targetType ->
                map[key[counter]] = targetType
                counter++
            }
        }
        return true
    }

    override fun register() {
        onSpawn.register()
    }

    override fun unregister() {
        onSpawn.unregister()
    }

    private val onSpawn = listen<CreatureSpawnEvent>(register = false) {
        val entity = it.entity
        if (it.spawnReason == CreatureSpawnEvent.SpawnReason.CUSTOM) return@listen
        val amount = entity.getNearbyEntities(10.0, 20.0, 10.0).size
        if (amount <= 6) {
            async {
                val loc = entity.location
                if (random) {
                    spawnMob(list.random(), loc)
                } else {
                    val type = map[it.entityType] ?: EntityType.ZOMBIE
                    spawnMob(type, loc)
                }
            }
        }
        it.isCancelled = true
    }

    private fun spawnMob(type: EntityType, location: Location) {
        sync {
            location.world.spawnEntity(location, type)
        }
    }
}