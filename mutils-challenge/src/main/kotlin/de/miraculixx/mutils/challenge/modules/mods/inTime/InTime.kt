package de.miraculixx.mutils.modules.challenge.mods.inTime

import de.miraculixx.mutils.challenge.utils.enums.Challenge
import de.miraculixx.mutils.challenge.modules.Challenge
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.onlinePlayers
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

class InTime : Challenge {
    override val challenge = Challenge.IN_TIME
    private var timers = HashMap<Entity, InTimeData>()
    private var mobTime = 1
    private var damageTime = 1
    private var playerTime = 1

    override fun start(): Boolean {
        val config = ConfigManager.getConfig(Configs.MODULES)
        mobTime = config.getInt("IN_TIME.MobTime")
        damageTime = config.getInt("IN_TIME.DamageTime")
        playerTime = config.getInt("IN_TIME.PlayerTime")
        for (player in onlinePlayers) {
            if (Spectator.isSpectator(player.uniqueId)) continue
            for (nearbyEntity in player.getNearbyEntities(50.0, 50.0, 50.0)) {
                if (nearbyEntity is Player) continue
                if (timers.containsKey(nearbyEntity)) continue
                val timer = InTimeData(mobTime, nearbyEntity, false)
                timers[nearbyEntity] = timer
            }
            if (player.gameMode == GameMode.SURVIVAL) {
                if (timers.containsKey(player)) continue
                val time = InTimeData(playerTime, player, true)
                timers[player] = time
            }
        }
        return true
    }

    override fun stop() {
        for (player in onlinePlayers) {
            if (timers[player] == null) continue
            timers[player]!!.remove()
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
        timers[it.player]?.remove()
        timers.remove(it.player)
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        val time = InTimeData(playerTime, it.player, true)
        timers[it.player] = time
    }

    //Damage Handling
    private val onDamage = listen<EntityDamageByEntityEvent>(register = false) {
        if (it.damager is Player) return@listen
        //Timer Check
        if (timers[it.entity] == null && it.entity.customName == null) {
            if (it.entity is Player) return@listen
            try {
                timers.remove(it.entity)
            } catch (exception: NullPointerException) {
                return@listen
            }
            val timer = InTimeData(mobTime, it.entity, false)
            timers[it.entity] = timer
        }
        if (timers[it.damager] == null && it.damager.customName == null) {
            if (it.damager is Player) return@listen
            try {
                timers.remove(it.damager)
            } catch (exception: NullPointerException) {
                return@listen
            }
            val timer = InTimeData(mobTime, it.damager, false)
            timers[it.damager] = timer
        }
        if (it.damager.type == EntityType.ARROW) {
            val entity = it.damager as Arrow
            if (entity.shooter !is LivingEntity) return@listen
            if (timers[entity.shooter as Entity] == null && (entity.shooter as Entity).customName == null) {
                if (entity.shooter is Player) return@listen
                try {
                    timers.remove(entity.shooter as Entity)
                } catch (exception: NullPointerException) {
                    return@listen
                }
                val timer = InTimeData(mobTime, entity.shooter as Entity, false)
                timers[entity.shooter as Entity] = timer
            }
        }
        //EnderDragon Fight
        if (it.damager.type == EntityType.AREA_EFFECT_CLOUD || it.damager.type == EntityType.ENDER_DRAGON) {
            if (it.entity.world.enderDragonBattle == null) return@listen
            if (it.entity.world.environment != World.Environment.THE_END) return@listen
            if (it.entity !is Player) return@listen
            var secTaker = timers[it.entity]?.sec //Timer von Damage Taker
            var secDamager = timers[it.entity.world.enderDragonBattle!!.enderDragon as Entity]!!.sec // Timer von Damager
            var damage: Double = it.damage //Damage wird mit Zeit berechnet
            if (secTaker == null) return@listen
            while (damage >= 1) {
                secTaker -= 5
                secDamager += 5
                damage -= 1.0
            }
            if (secTaker <= 0) secTaker = 0
            var minTaker = 0 //Zeit Formatierung von Damage Taker
            while (secTaker >= 60) {
                secTaker -= 60
                minTaker += 1
            }
            var minDamager = 0 //Zeit Formatierung von Damager
            while (secDamager >= 60) {
                secDamager -= 60
                minDamager += 1
            }
            timers[it.entity]!!.setTime(minTaker, secTaker)
            timers[it.entity.world.enderDragonBattle!!.enderDragon as Entity]!!.setTime(minDamager, secDamager) // Timer von Damager
            return@listen
        }

        //Default vvv
        if (it.damager is LivingEntity || it.damager.type == EntityType.ARROW) {
            var entity: Entity = it.damager
            if (it.damager.type == EntityType.ARROW) { //Wenn Arrow, bekomme Shooter
                if ((it.damager as Arrow).shooter !is LivingEntity) return@listen
                entity = (it.damager as Arrow).shooter as Entity
            }
            var secTaker = timers[it.entity]!!.sec //Timer von Damage Taker
            var secDamager = timers[entity]!!.sec // Timer von Damager
            var damage: Double = it.damage //Damage wird mit Zeit berechnet
            while (damage >= 1) {
                secTaker -= damageTime
                secDamager += (damageTime / 2)
                damage -= 1.0
            }
            if (secTaker <= 0) secTaker = 0
            var minTaker = 0 //Zeit Formatierung von Damage Taker
            while (secTaker >= 60) {
                secTaker -= 60
                minTaker += 1
            }
            var minDamager = 0 //Zeit Formatierung von Damager
            while (secDamager >= 60) {
                secDamager -= 60
                minDamager += 1
            }
            timers[it.entity]!!.setTime(minTaker, secTaker)
            timers[entity]!!.setTime(minDamager, secDamager)
        }
    }

    private val onMerge = listen<ItemMergeEvent>(register = false) {
        it.isCancelled = true
    }

    private val onKill = listen<EntityDamageByEntityEvent>(register = false) {
        if (it.damager.type == EntityType.ARROW) {
            if ((it.entity as LivingEntity).health - it.damage <= 0.0) {
                if ((it.damager as Arrow).shooter !is Player) return@listen
                val player: Player = (it.damager as Arrow).shooter as Player
                var sec = timers[it.entity]!!.sec
                sec += timers[player]!!.sec
                var min = 0
                while (sec >= 60) {
                    sec -= 60
                    min += 1
                }
                timers[player]!!.setTime(min, sec)
            }
        }
        if (it.entity !is LivingEntity) return@listen
        if (it.damager !is Player) return@listen
        if ((it.entity as LivingEntity).health - it.damage <= 0.0) {
            var sec = timers[it.entity]!!.sec
            sec += timers[it.damager]!!.sec
            var min = 0
            while (sec >= 60) {
                sec -= 60
                min += 1
            }
            timers[it.damager]!!.setTime(min, sec)
        }
    }

    private val onSpawn = listen<CreatureSpawnEvent>(register = false) {
        val type: EntityType = it.entityType
        if (type == EntityType.ARROW || type == EntityType.SPECTRAL_ARROW || type == EntityType.TRIDENT || type == EntityType.SNOWBALL || type == EntityType.SPLASH_POTION) return@listen
        val timer = InTimeData(mobTime, it.entity, false)
        timers[it.entity] = timer
    }

    private val onVehicleCreate = listen<VehicleCreateEvent>(register = false) {
        val timer = InTimeData(mobTime, it.vehicle, false)
        timers[it.vehicle] = timer
    }

    private val onVehicleEnter = listen<VehicleEnterEvent>(register = false) {
        if (it.entered is Player) return@listen
        it.isCancelled = true
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        for (nearbyEntity in it.player.getNearbyEntities(30.0, 20.0, 30.0)) {
            if (timers[nearbyEntity] == null && nearbyEntity.customName == null) {
                if (nearbyEntity is Player) return@listen
                try {
                    timers.remove(nearbyEntity)
                } catch (exception: NullPointerException) {
                    return@listen
                }
                val timer = InTimeData(mobTime, nearbyEntity, false)
                timers[nearbyEntity] = timer
            }
        }
    }
}