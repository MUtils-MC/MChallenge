package de.miraculixx.mutils.modules.challenge.mods.gravity

import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

@Suppress("LABEL_NAME_CLASH")
class NoGravity : Gravity {
    override var active = true
    override fun start() {
        onlinePlayers.forEach { p ->
            p.setGravity(true)
            p.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 20, 0, false, false, false))
            p.getNearbyEntities(300.0, 200.0, 300.0).forEach { entity ->
                if (entity is Player) return@forEach
                entity.setGravity(false)
                if (entity is LivingEntity) {
                    entity.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 20, 0, false, false))
                } else {
                    val vector = Vector(0.0, 0.2, 0.0)
                    entity.velocity = vector
                }
            }
        }
    }


    private val onMove = listen<PlayerMoveEvent> {
        val player = it.player
        if (player.isSneaking) {
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 5, 0, false, false, false))
            player.removePotionEffect(PotionEffectType.LEVITATION)
            if (player.velocity.y > -0.1) {
                player.velocity.y = player.velocity.y - 0.01
            }
        } else {
            player.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, Int.MAX_VALUE, 0, false, false, false))
            if (it.to.block.type == Material.WATER) {
                val vector = Vector(0.0, 0.05, 0.0)
                player.velocity = player.velocity.clone().add(vector)
            }
            player.getNearbyEntities(10.0, 10.0, 10.0).forEach { entity ->
                if (entity.type == EntityType.BOAT) {
                    entity.setGravity(false)
                    entity.velocity = entity.velocity.clone().setY(0.02)
                }
            }
        }
    }

    private val onDrop = listen<PlayerDropItemEvent> {
        it.itemDrop.setGravity(false)
        it.itemDrop.velocity.y = 0.1
    }

    private val onBreak = listen<BlockBreakEvent> {
        val block = it.block
        block.drops.forEach { drop ->
            val item = block.world.dropItem(block.location.add(.5,.5,.5), drop)
            item.setGravity(false)
            val vector = Vector(0.0, 0.2, 0.0)
            item.velocity = vector
        }
        it.isDropItems = false
    }

    private val onShoot = listen<EntityShootBowEvent> {
        val proj = it.projectile
        proj.setGravity(false)
        val vector = Vector(0.0, 0.1, 0.0)
        proj.velocity = proj.velocity.clone().add(vector)
    }

    private val onMerge = listen<ItemMergeEvent> {
        it.isCancelled = true
    }

    private val onMobSpawn = listen<CreatureSpawnEvent> {
        it.entity.setGravity(false)
        it.entity.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 20, 0, false, false))
    }

    private val onMobDrop = listen<EntityDeathEvent> {
        val entity = it.entity
        it.drops.forEach { drop ->
            val item = entity.world.dropItem(entity.location, drop)
            item.setGravity(false)
            item.velocity.y = 0.2
        }
        it.drops.clear()
    }

    override val classes = listOf(onDrop, onBreak, onMobDrop, onMerge, onMobSpawn, onMove, onShoot)
}