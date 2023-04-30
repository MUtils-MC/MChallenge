package de.miraculixx.mchallenge.modules.mods.inTime

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.challenge.api.modules.challenges.Challenges
import de.miraculixx.mvanilla.messages.cError
import de.miraculixx.mvanilla.messages.cHighlight
import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.challenge.api.settings.challenges
import de.miraculixx.challenge.api.settings.getSetting
import org.bukkit.GameMode
import org.bukkit.World
import org.bukkit.entity.*
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.vehicle.VehicleCreateEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import java.util.UUID

class InTime : Challenge {
    private var timers = HashMap<UUID, InTimeData>()
    private var mobTime: Int
    private var damageTime: Int
    private var playerTime: Int

    init {
        val settings = challenges.getSetting(Challenges.IN_TIME).settings
        mobTime = settings["pTime"]?.toInt()?.getValue() ?: 120
        damageTime = settings["eTime"]?.toInt()?.getValue() ?: 120
        playerTime = settings["hpTime"]?.toInt()?.getValue() ?: 5
    }

    override fun start(): Boolean {
        onlinePlayers.forEach { player ->
            if (Spectator.isSpectator(player.uniqueId)) return@forEach
            player.world.entities.forEach entity@{ entity ->
                if (entity is Player) return@entity
                val uuid = entity.uniqueId
                if (timers.containsKey(uuid)) return@entity
                val timer = InTimeData(mobTime, entity, false)
                timers[uuid] = timer
            }
            if (player.gameMode == GameMode.SURVIVAL) {
                val uuid = player.uniqueId
                if (timers.containsKey(uuid)) return@forEach
                val time = InTimeData(playerTime, player, true)
                timers[uuid] = time
            }
        }
        return true
    }

    override fun stop() {
        onlinePlayers.forEach { player ->
            val uuid = player.uniqueId
            if (timers.containsKey(uuid)) timers[uuid]?.remove()
        }
        timers.clear()
    }

    override fun register() {
        onLeave.register()
        onJoin.register()
        onDamage.register()
        onMerge.register()
        onKill.register()
        onSpawn.register()
        onVehicleCreate.register()
        onVehicleEnter.register()
        onMove.register()
    }
    override fun unregister() {
        onLeave.unregister()
        onJoin.unregister()
        onDamage.unregister()
        onMerge.unregister()
        onKill.unregister()
        onSpawn.unregister()
        onVehicleCreate.unregister()
        onVehicleEnter.unregister()
        onMove.unregister()
    }

    //Connection Handling
    private val onLeave = listen<PlayerQuitEvent>(register = false) {
        timers[it.player.uniqueId]?.pauseTimer()
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        getTimer(it.player, true).resumeTimer()
    }

    //Damage Handling
    private val onDamage = listen<EntityDamageByEntityEvent>(register = false) {
        val damager = it.damager
        if (damager is Player) return@listen
        val entity = it.entity

        //Timer Check
        checkEntity(entity) // Damaged Entity
        checkEntity(damager) // Damager Entity
        if (damager is Projectile) { // Receive damager by projectile
            val shooter = damager.shooter
            if (shooter !is LivingEntity) return@listen
            val uuid = shooter.uniqueId
            if (timers[uuid] == null && shooter.customName() == null) {
                if (shooter is Player) return@listen
                timers.remove(uuid)
                val timer = InTimeData(mobTime, shooter, false)
                timers[uuid] = timer
            }
        }

        //EnderDragon Fight
        if (damager is AreaEffectCloud || damager is EnderDragon) {
            if (entity.world.enderDragonBattle == null) return@listen
            if (entity.world.environment != World.Environment.THE_END) return@listen
            if (entity !is Player) return@listen
            entity.world.enderDragonBattle?.enderDragon?.let { dragon -> handleTime(dragon, entity, it.finalDamage.toInt()) }
            return@listen
        }

        //Projectiles
        if (damager is Projectile) {
            val shooter = damager.shooter
            if (shooter !is LivingEntity) return@listen
            handleTime(shooter, entity, it.finalDamage.toInt())
            return@listen
        }

        //Default
        if (damager is LivingEntity) {
            handleTime(damager, entity, it.finalDamage.toInt())
        }
    }

    private val onMerge = listen<ItemMergeEvent>(register = false) {
        it.isCancelled = true
    }

    private val onKill = listen<EntityDamageByEntityEvent>(register = false) {
        val damager = it.damager
        val taker = it.entity
        if (taker !is LivingEntity) return@listen
        if (damager is Projectile) {
            if (taker.health - it.finalDamage <= 0.0) {
                val shooter = damager.shooter
                if (shooter !is Player) return@listen
                val timerPlayer = getTimer(shooter)
                var sec = getTimer(taker).sec
                sec += timerPlayer.sec
                var min = 0
                while (sec >= 60) {
                    sec -= 60
                    min += 1
                }
                timerPlayer.setTime(min, sec)
            }
        }

        if (damager !is Player) return@listen
        if (taker.health - it.finalDamage <= 0.0) {
            val timerTaker = getTimer(taker)
            var sec = timerTaker.sec
            sec += getTimer(damager).sec
            var min = 0
            while (sec >= 60) {
                sec -= 60
                min += 1
            }
            timerTaker.setTime(min, sec)
        }
    }

    private val onSpawn = listen<CreatureSpawnEvent>(register = false) {
        val entity = it.entity
        if (entity is Projectile) return@listen
        timers[entity.uniqueId] = InTimeData(mobTime, entity, false)
    }

    private val onVehicleCreate = listen<VehicleCreateEvent>(register = false) {
        val timer = InTimeData(mobTime, it.vehicle, false)
        timers[it.vehicle.uniqueId] = timer
    }

    private val onVehicleEnter = listen<VehicleEnterEvent>(register = false) {
        if (it.entered is Player) return@listen
        it.isCancelled = true
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        it.player.getNearbyEntities(15.0,15.0,15.0).forEach { entity ->
            if (entity is Player) return@listen
            entity.isCustomNameVisible = true
            val timer = getTimer(entity)
            val color = if (timer.isRed()) cError else cHighlight
            entity.customName(cmp(timer.getTime(), color))
        }
    }

    private fun checkEntity(entity: Entity) {
        val uuid = entity.uniqueId
        if (timers[uuid] == null && entity.customName() == null) {
            if (entity is Player) return
            timers.remove(uuid)
            val timer = InTimeData(mobTime, entity, false)
            timers[uuid] = timer
        }
    }

    private fun getTimer(entity: Entity, isPlayer: Boolean = false): InTimeData {
        val uuid = entity.uniqueId
        return if (timers.containsKey(uuid)) timers[uuid]!! else {
            val new = InTimeData(if (isPlayer) playerTime else mobTime, entity, isPlayer)
            timers[uuid] = new
            new
        }
    }

    private fun handleTime(damager: Entity, taker: Entity, damage: Int) {
        val timerTaker = getTimer(taker)
        val timerDamager = getTimer(damager)
        var secTaker = timerTaker.sec // Timer from Damage Taker
        var secDamager = timerDamager.sec // Timer from Damager
        secTaker -= damageTime * damage
        secDamager += (damageTime / 2) * damage
        if (secTaker <= 0) secTaker = 0
        var minTaker = 0 // Time formatting from Damage Taker
        while (secTaker >= 60) {
            secTaker -= 60
            minTaker += 1
        }
        var minDamager = 0 // Time formatting from Damager
        while (secDamager >= 60) {
            secDamager -= 60
            minDamager += 1
        }

        timerTaker.setTime(minTaker, secTaker)
        timerDamager.setTime(minDamager, secDamager)
    }
}