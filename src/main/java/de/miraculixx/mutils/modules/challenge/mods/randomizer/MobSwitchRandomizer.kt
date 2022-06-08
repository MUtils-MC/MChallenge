package de.miraculixx.mutils.modules.challenge.mods.randomizer

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.worlds
import net.axay.kspigot.runnables.async
import net.axay.kspigot.runnables.sync
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent
import java.util.*
import kotlin.random.Random

class MobSwitchRandomizer : Challenge() {
    override val challenge = Modules.RANDOMIZER_MOBS
    private var random = false
    private var list: List<EntityType>? = null
    private val map = HashMap<EntityType, EntityType>()
    private var status = ChallengeStatus.PAUSED

    override fun start(): Boolean {
        val c = ConfigManager.getConfig(Configs.MODULES)
        val rnd = Random(worlds.first().seed)
        random = c.getBoolean("MOB_RANDOMIZER.Random")
        val key = kotlin.collections.ArrayList<EntityType>(Arrays.stream(EntityType.values()).filter { type ->
            type.entityClass != null && LivingEntity::class.java.isAssignableFrom(type.entityClass) && type != EntityType.PLAYER &&
                    type != EntityType.ENDER_DRAGON
        }.toList()).shuffled(rnd)

        if (random) list = key
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

    override fun stop() {
        list = null
        map.clear()
        status = ChallengeStatus.STOPPED
    }

    override fun register() {
        status = ChallengeStatus.RUNNING
        onSpawn.register()
    }

    override fun unregister() {
        status = ChallengeStatus.PAUSED
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
                    spawnMob(list?.random(Random(entity.world.seed)) ?: EntityType.ZOMBIE, loc)
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