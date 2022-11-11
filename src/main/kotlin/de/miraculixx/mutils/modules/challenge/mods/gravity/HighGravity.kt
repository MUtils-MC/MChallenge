package de.miraculixx.mutils.modules.challenge.mods.gravity

import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.task
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.BlastFurnace
import org.bukkit.block.Chest
import org.bukkit.block.Furnace
import org.bukkit.block.Smoker
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

class HighGravity : Gravity {
    override var active = true
    override fun start() {
        onlinePlayers.forEach { p ->
            p.setGravity(true)
            p.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 99999, 200, false, false, false))
        }
        mobPusher()
    }

    private fun mobPusher() {
        val vector = Vector(0.0, -0.1, 0.0)
        task(true, 2, 2) {
            if (!active) {
                it.cancel()
                return@task
            }
            onlinePlayers.forEach { p ->
                p.getNearbyEntities(30.0, 20.0, 30.0).forEach { entity ->
                    if (entity is Player) return@forEach
                    val result = entity.velocity.clone().add(vector)
                    entity.velocity = result
                }
            }
        }
        task(true, 2, 200) {
            if (!active) {
                it.cancel()
                return@task
            }
            onlinePlayers.forEach { p ->
                p.getNearbyEntities(50.0, 30.0, 50.0).forEach { entity ->
                    if (entity is LivingEntity && entity !is Player)
                        entity.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 99999, 0, false, false))
                }
            }
        }
    }

    private val onClick = listen<PlayerInteractEvent> {
        if (it.clickedBlock == null) return@listen
        if (it.action.isLeftClick) return@listen
        val block = it.clickedBlock!!
        val player = it.player
        taskRunLater(1) {
            when (block.type) {
                Material.CRAFTING_TABLE -> player.openWorkbench(block.location, false)
                Material.FURNACE -> player.openInventory((block as Furnace).inventory)
                Material.BLAST_FURNACE -> player.openInventory((block as BlastFurnace).inventory)
                Material.SMOKER -> player.openInventory((block as Smoker).inventory)
                Material.CHEST -> player.openInventory((block as Chest).inventory)
                else -> return@taskRunLater
            }
        }
    }

    private val onMove = listen<PlayerMoveEvent> {
        val player = it.player
        player.getNearbyEntities(0.5, 1.0, 0.5).forEach { entity ->
            if (entity.type == EntityType.DROPPED_ITEM) {
                if ((entity as Item).pickupDelay > 0) return@listen
                player.playPickupItemAnimation(entity)
                //player.playSound(player.location, Sound.ENTITY_ITEM_PICKUP, 0.2f, 2f)
                entity.remove()
            } else if (entity.type == EntityType.BOAT) {
                entity.setGravity(true)
                entity.velocity = entity.velocity.clone().setY(-0.05)
            }
        }
        if (player.gameMode != GameMode.SURVIVAL) return@listen

        val subblock = it.to.clone().add(.0,-1.0,.0).block.type
        if (subblock.isAir || subblock == Material.WATER) {
            player.removePotionEffect(PotionEffectType.LEVITATION)
            val vector = Vector(0, -1, 0)
            player.velocity = vector
        } else {
            player.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 99999, 200, false, false, false))
        }
    }

    override val classes = listOf(onClick, onMove)
}