package de.miraculixx.mchallenge.modules.mods.misc.rhythm.entity

import de.miraculixx.mvanilla.messages.cmp
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.joml.Vector2f

abstract class RLivingEntity(private val entity: LivingEntity) {

    open fun tick() {
        if (entity.fireTicks > 0) entity.damage(1.0)
    }

    fun normalize() {
        entity.teleport(entity.location.block.centerLocation())
    }

    open fun onSpawn() {
        normalize()
        entity.setAI(false)
        entity.setGravity(false)
        entity.isGlowing = true
        entity.isCustomNameVisible = true
    }

    open fun stop() {
        entity.setAI(true)
        entity.setGravity(true)
    }

    fun checkDead() = entity.isDead

    fun moveDirection(direction: BlockFace, view: Vector2f? = null, freeMode: Boolean = false): Boolean {
        val currentBlock = entity.location.block
        val directionBlock = currentBlock.getRelative(direction)

        // Calculate walkable block
        val lower = currentBlock.getRelative(BlockFace.DOWN)
        var isFalling = false
        val targetLocation = when {
            // Check if floating currently (only move down)
            lower.isPassable -> {
                isFalling = true
                lower.centerLocation()
            }

            // Move in direction or down
            directionBlock.isPassable -> {
                val height = calculateHeight(directionBlock)
                when {
                    height == 0 -> directionBlock.centerLocation()
                    height in 1..2 || freeMode -> {
                        directionBlock.getRelative(BlockFace.DOWN).centerLocation()
                    }

                    else -> currentBlock.centerLocation()
                }
            }

            // Move up
            else -> {
                val upper = directionBlock.getRelative(BlockFace.UP)
                if (upper.isPassable) {
                    upper.centerLocation()
                } else currentBlock.centerLocation()
            }
        }

        // Check for other entities
        val isOccupied = targetLocation.getNearbyLivingEntities(0.1).isNotEmpty()
        val finalLocation = if (isOccupied) currentBlock.centerLocation() else targetLocation
        checkFallDamage(finalLocation.block, isFalling)

        // Apply facing direction and teleport
        entity.teleport(finalLocation.apply {
            if (view != null) {
                this.yaw = view.x
                this.pitch = view.y
                return@apply
            }
            when (direction) {
                BlockFace.EAST -> this.yaw = 270.0f
                BlockFace.WEST -> this.yaw = 90.0f
                BlockFace.NORTH -> this.yaw = 180.0f
                BlockFace.SOUTH -> this.yaw = 0.0f
                else -> Unit
            }
        })
        return isOccupied
    }

    private fun calculateHeight(block: Block): Int {
        var height = 0
        var currentBlock = block.getRelative(BlockFace.DOWN)
        while (currentBlock.isPassable && height <= 3) {
            height += 1
            currentBlock = currentBlock.getRelative(BlockFace.DOWN)
        }
        return height
    }

    private fun checkFallDamage(destination: Block, isFalling: Boolean) {
        if (isFalling || destination.getRelative(BlockFace.DOWN).isPassable) {
            entity.fallDistance += 1f
        } else {
            if (entity.fallDistance > 2f) {
                entity.damage(1.0)
            }
            entity.fallDistance = 0f
        }
    }

    private fun Block.centerLocation() = location.add(0.5, 0.0, 0.5)
}