package de.miraculixx.mutils.modules.mods.gravity

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.taskRunLater
import org.bukkit.Statistic
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

class LowGravity : Gravity {
    override var active = true

    override fun start() {
        onlinePlayers.forEach { p -> modifyPlayer(p) }
    }

    override fun modifyPlayer(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 999999, 0, false, false, false))
        for (entity in player.getNearbyEntities(300.0, 200.0, 300.0)) {
            if (entity is Player) continue
            if (entity is LivingEntity) {
                entity.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, Int.MAX_VALUE, 99, false, false, false))
                entity.addPotionEffect(PotionEffect(PotionEffectType.JUMP, Int.MAX_VALUE, 4, false, false, false))
                entity.addPotionEffect(PotionEffect(PotionEffectType.SLOW, Int.MAX_VALUE, 0, false, false, false))
            } else {
                entity.setGravity(false)
                val vector = Vector(0.0, -0.1, 0.0)
                entity.velocity = vector
            }
        }
    }


    private val onDrop = listen<PlayerDropItemEvent> {
        it.itemDrop.setGravity(false)
        val vector = it.itemDrop.velocity.clone().setY(it.itemDrop.velocity.y - 0.1)
        it.itemDrop.velocity = vector
    }

    private val onHit = listen<EntityDamageByEntityEvent> {
        if (it.entity !is LivingEntity) return@listen
        val entity = it.entity as LivingEntity
        val vector = entity.velocity.clone().multiply(2)
        entity.velocity = entity.velocity.add(vector)
        entity.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 5, 10, false, false, false))
    }


    private val onKill = listen<EntityDeathEvent> {
        it.drops.forEach { drop ->
            val item = it.entity.world.dropItem(it.entity.location, drop)
            item.setGravity(false)
            val vector = Vector(0.0, 0.4, 0.0)
            item.velocity = vector
        }
        it.drops.clear()
    }


    private val onSpawn = listen<CreatureSpawnEvent> {
        val entity = it.entity
        entity.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 999999, 99, false, false, false))
        entity.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 999999, 4, false, false, false))
        entity.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 999999, 0, false, false, false))
    }


    private val onShoot = listen<EntityShootBowEvent> {
        it.projectile.setGravity(false)
    }


    private val onBreak = listen<BlockBreakEvent> {
        val block = it.block
        block.drops.forEach { drop ->
            val item = block.world.dropItem(block.location, drop)
            item.pickupDelay = 15
            item.setGravity(false)
        }
        it.isDropItems = false
    }


    private val onMerge = listen<ItemMergeEvent> {
        it.isCancelled = true
    }


    private val onMove = listen<PlayerMoveEvent> {
        val player = it.player
        player.getNearbyEntities(50.0, 30.0, 50.0).forEach { entity ->
            if (entity is LivingEntity) return@forEach
            val vector = Vector(0.0, -0.01, 0.0)
            entity.velocity = entity.velocity.clone().add(vector)
            if (entity.type == EntityType.BOAT) {
                entity.setGravity(false)
                entity.velocity = entity.velocity.clone().setY(0.01)
            }
        }
        if (player.getStatistic(Statistic.JUMP) > 0) {
            player.setGravity(false)
            player.removePotionEffect(PotionEffectType.JUMP)
            player.setStatistic(Statistic.JUMP, 0)
            delay(player)
        }
        if (player.velocity.y == -0.0784000015258789) {
            player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 99999, 3, false, false, false))
        }
        if (player.velocity.y < -0.08) {
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 5, 0, false, false, false))
        }
        if (player.velocity.y == 0.0) {
            player.setGravity(true)
        }
    }


    //Utilitys
    private fun delay(p: Player) {
        taskRunLater(30) {
            if (!active) return@taskRunLater
            p.setGravity(true)
            p.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 99999, 3, false, false, false))
        }
    }

    override val classes = listOf(
        onDrop, onHit, onBreak, onKill, onSpawn, onShoot, onMove, onMerge
    )
}