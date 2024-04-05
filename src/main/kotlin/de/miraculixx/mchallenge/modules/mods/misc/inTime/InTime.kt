package de.miraculixx.mchallenge.modules.mods.misc.inTime

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import org.bukkit.World
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.vehicle.VehicleCreateEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import java.util.*
import kotlin.collections.HashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds

class InTime : Challenge {
    private var timers = HashMap<UUID, InTimeEntity>()
    private var mobTime: Duration
    private var playerTime: Duration
    private var damageTime: Duration

    init {
        val settings = challenges.getSetting(Challenges.IN_TIME).settings
        mobTime = (settings["pTime"]?.toInt()?.getValue() ?: 120).seconds
        playerTime = (settings["eTime"]?.toInt()?.getValue() ?: 120).seconds
        damageTime = (settings["hpTime"]?.toInt()?.getValue() ?: 5).seconds
    }

    override fun start(): Boolean {
        worlds.forEach { world ->
            world.entities.forEach entity@{ entity ->
                if (entity is Player) return@entity
                val timer = InTimeEntity(mobTime, entity, false)
                timers[entity.uniqueId] = timer
            }
        }

        onlinePlayers.forEach { p ->
            val timer = InTimeEntity(playerTime, p, true)
            timers[p.uniqueId] = timer
        }

        return true
    }

    override fun stop() {
        timers.forEach { (_, timer) -> timer.remove() }
        timers.clear()
        task?.cancel()
    }

    override fun register() {
        timers.forEach { (_, timer) -> timer.isRunning = true }
        onLeave.register()
        onJoin.register()
        onDamage.register()
        onSpawn.register()
        onMerge.register()
        onVehicleCreate.register()
        onVehicleEnter.register()
        onMove.register()
    }

    override fun unregister() {
        timers.forEach { (_, timer) -> timer.isRunning = false }
        onLeave.unregister()
        onJoin.unregister()
        onDamage.unregister()
        onSpawn.unregister()
        onMerge.unregister()
        onVehicleCreate.unregister()
        onVehicleEnter.unregister()
        onMove.unregister()
    }



    private fun getTimer(entity: Entity, isPlayer: Boolean = false): InTimeEntity {
        val uuid = entity.uniqueId
        return timers[uuid] ?: InTimeEntity(if (isPlayer) playerTime else mobTime, entity, isPlayer).also { timers[uuid] = it }
    }

    //
    // Connection Handling
    //
    private val onLeave = listen<PlayerQuitEvent>(register = false) {
        timers[it.player.uniqueId]?.isRunning = false
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        getTimer(it.player, true).isRunning = true
    }


    //
    // Damage Handling
    //
    private val onDamage = listen<EntityDamageByEntityEvent>(register = false) {
        val damager = it.damager
        val target = it.entity
        if (target !is LivingEntity) return@listen

        val finalDamager = when (damager) {
            is Projectile -> {
                val shooter = damager.shooter ?: return@listen
                if (shooter !is LivingEntity || shooter is Player) return@listen
                shooter
            }

            is AreaEffectCloud, is EnderDragon -> {
                val world = target.world
                if (world.environment != World.Environment.THE_END || target !is Player) return@listen
                world.enderDragonBattle?.enderDragon ?: return@listen
            }

            is LivingEntity -> damager

            else -> return@listen
        }

        val damagerTime = getTimer(finalDamager)
        val targetTime = getTimer(target)
        val isKill = (target.health - it.finalDamage) <= 0

        if (isKill) {
            damagerTime.duration += targetTime.duration
            if (target is Player) {
                targetTime.duration = ZERO
                it.damage = 0.0
            } else targetTime.remove()
        } else {
            val damageDuration = damageTime * it.finalDamage
            if (targetTime.duration < damageDuration) { // Do not grant more time than existing
                damagerTime.duration += targetTime.duration
                targetTime.duration = ZERO
            } else {
                targetTime.duration -= damageDuration
                damagerTime.duration += damageDuration
            }
        }
    }


    //
    // Entity Spawn Behavior
    //
    private val onSpawn = listen<CreatureSpawnEvent>(register = false) {
        val entity = it.entity
        timers[entity.uniqueId] = InTimeEntity(mobTime, entity, false)
    }

    private val onMerge = listen<ItemMergeEvent>(register = false) {
        it.isCancelled = true
    }

    private val onVehicleCreate = listen<VehicleCreateEvent>(register = false) {
        val vehicle = it.vehicle
        timers[vehicle.uniqueId] = InTimeEntity(mobTime, vehicle, false)
    }

    private val onVehicleEnter = listen<VehicleEnterEvent>(register = false) {
        if (it.entered !is Player) it.isCancelled = true
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        if (!it.hasChangedPosition()) return@listen
        it.player.getNearbyEntities(15.0, 15.0, 15.0).forEach { e ->
            if (e !is Player) getTimer(e)
        }
    }


    //
    // Name visibility
    //
    private val task = task(true, 20, 20) {
        val visibleEntities = buildSet {
            onlinePlayers.forEach { p ->
                p.getNearbyEntities(15.0, 15.0, 15.0).forEach entities@{ e ->
                    if (e is Player) return@entities
                    add(e.uniqueId)
                    if (e.customName() != null) e.isCustomNameVisible = true
                }
            }
        }
        timers.forEach { (uuid, timer) ->
            if (uuid !in visibleEntities) timer.entity.isCustomNameVisible = false
        }
    }
}