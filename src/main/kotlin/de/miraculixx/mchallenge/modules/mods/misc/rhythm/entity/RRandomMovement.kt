package de.miraculixx.mchallenge.modules.mods.misc.rhythm.entity

import org.bukkit.block.BlockFace
import org.bukkit.entity.LivingEntity
import kotlin.random.Random

abstract class RRandomMovement(private val entity: LivingEntity): RLivingEntity(entity) {
    private val directions = setOf(
        BlockFace.EAST,
        BlockFace.WEST,
        BlockFace.NORTH,
        BlockFace.SOUTH
    )

    fun moveRandom() {
        if (entity.fireTicks > 0 || Random.nextBoolean()) return
        moveDirection(directions.random())
    }
}