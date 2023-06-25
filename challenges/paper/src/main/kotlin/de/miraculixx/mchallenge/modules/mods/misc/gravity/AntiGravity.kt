package de.miraculixx.mchallenge.modules.mods.misc.gravity

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mvanilla.messages.msg
import org.bukkit.Material
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

class AntiGravity : Gravity {
    override var active = true
    override fun start() {
        onlinePlayers.forEach { p -> modifyPlayer(p) }
    }

    override fun modifyPlayer(player: Player) {
        player.setGravity(false)
        player.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 99999, 2, false, false, false))
        player.getNearbyEntities(300.0, 200.0, 300.0).forEach entities@{ entity ->
            if (entity is Player) return@entities
            entity.setGravity(false)
            if (entity is LivingEntity) {
                entity.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 99999, 2, false, false))
            } else {
                val vector = Vector(0.0, 0.5, 0.0)
                entity.velocity = vector
            }
        }
    }

    private val onMove = listen<PlayerMoveEvent> {
        val player = it.player
        if (it.to.blockY > 250) {
            player.addPotionEffect(PotionEffect(PotionEffectType.WITHER, 20, 2, false, false, false))
        }
        player.getNearbyEntities(50.0, 30.0, 50.0).forEach { entity ->
            if (entity !is LivingEntity) {
                val vector = entity.velocity.clone().setY(0.3)
                entity.velocity = vector
                if (entity.type == EntityType.BOAT) {
                    entity.setGravity(false)
                    entity.velocity = entity.velocity.clone().setY(0.06)
                }
            }
        }
        if (it.to.block.type == Material.WATER) {
            val vector = Vector(0.0, 0.15, 0.0)
            player.velocity = player.velocity.clone().add(vector)
        }
    }

    private val onShoot = listen<EntityShootBowEvent> {
        val proj = it.projectile
        proj.setGravity(false)
        val vector = Vector(0.0, 0.6, 0.0)
        proj.velocity = proj.velocity.clone().add(vector)
    }

    private val onDeath = listen<PlayerDeathEvent> {
        if (it.player.location.blockY > 250) {
            it.deathMessage(msg("event.death.gravity", listOf(it.player.name)))
        }
    }

    private val onDrop = listen<PlayerDropItemEvent> {
        it.itemDrop.setGravity(false)
        it.itemDrop.velocity.y = 0.3
    }

    private val onBreak = listen<BlockBreakEvent> {
        val block = it.block
        it.block.drops.forEach { drop ->
            val item = block.world.dropItem(block.location, drop)
            item.setGravity(false)
            val vector = Vector(0.0, 0.4, 0.0)
            item.velocity = vector
        }
        it.isDropItems = false
    }

    private val onMerge = listen<ItemMergeEvent> {
        it.isCancelled = true
    }

    private val onMobSpawn = listen<EntitySpawnEvent> {
        val entity = it.entity
        entity.setGravity(false)
        if (entity is LivingEntity)
            entity.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 99999, 2, false, false))
    }

    private val onMobDrop = listen<EntityDeathEvent> {
        val entity = it.entity
        it.drops.forEach { drop ->
            val item = entity.world.dropItem(entity.location, drop)
            item.setGravity(false)
            item.velocity.y = 0.3
        }
        it.drops.clear()
    }

    override val classes = listOf(onDrop, onDeath, onBreak, onMobDrop, onMobSpawn, onMerge, onMove, onShoot)
}