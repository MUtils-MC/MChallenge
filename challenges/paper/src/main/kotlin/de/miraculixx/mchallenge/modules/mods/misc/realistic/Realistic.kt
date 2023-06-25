package de.miraculixx.mchallenge.modules.mods.misc.realistic

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.runnables.taskRunLater
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.random.Random

class Realistic : Challenge {

    override fun register() {
        onBreak.register()
        onExplode.register()
        onEntityExplode.register()
        onPlace.register()
    }

    override fun unregister() {
        onBreak.unregister()
        onExplode.unregister()
        onEntityExplode.unregister()
        onPlace.unregister()
    }

    private val onBreak = listen<BlockBreakEvent>(register = false) {
        val block = it.block
        val blockType = block.type

        if (Tag.LOGS.isTagged(blockType) || blockType == Material.MUSHROOM_STEM) {
            taskRunLater(1) { chopTree(block, mutableSetOf()) }
            return@listen
        }

        checkPhysics(block.location.clone().add(0.0, 1.0, 0.0).block)
    }

    private val onPlace = listen<BlockPlaceEvent>(register = false) {
        val block = it.block
        val sourceLoc = block.location
        val lowerBlock = sourceLoc.clone().add(.0, -1.0, .0).block

        if (lowerBlock.type.isAir) {
            sourceLoc.world.spawnFallingBlock(sourceLoc.add(0.5, 0.0, 0.5), block.blockData)
            block.type = Material.AIR
            return@listen
        }
    }

    private fun chopTree(sourceLog: Block, processedBlocks: MutableSet<Block>) {
        processedBlocks.add(sourceLog)
        val sourceLoc = sourceLog.location
        if (!sourceLog.type.isAir) {
            sourceLog.world.spawnFallingBlock(sourceLoc.add(.5, .2, .5), sourceLog.blockData)
            sourceLog.type = Material.AIR
        }

        // Chop Horizontally
        (-3..3).forEach { z ->
            (-3..3).forEach x@{ x ->
                if (z == 0 && x == 0) return@x
                val targetBlock = sourceLoc.clone().add(x.toDouble(), .0, z.toDouble()).block
                val targetType = targetBlock.type
                val isLog = Tag.LOGS.isTagged(targetType)

                if (Tag.COMPLETES_FIND_TREE_TUTORIAL.isTagged(targetType) && !isLog) {
                    val fb = targetBlock.world.spawnFallingBlock(targetBlock.location.add(.5, .2, .5), targetBlock.blockData)
                    fb.dropItem = false
                    targetBlock.type = Material.AIR

                } else if (isLog && !processedBlocks.contains(targetBlock)) {
                    // Log detected on same y level -> chop it
                    taskRunLater(1) { chopTree(targetBlock, processedBlocks) }
                }
            }
        }

        // Chop Vertically
        val topBlock = sourceLoc.clone().add(.0, 1.0, .0).block
        val topBlockType = topBlock.type
        if (Tag.COMPLETES_FIND_TREE_TUTORIAL.isTagged(topBlockType) && !processedBlocks.contains(topBlock)) {
            taskRunLater(1) { chopTree(topBlock, processedBlocks) }
        }
    }

    private fun checkPhysics(sourceBlock: Block) {
        val sourceLoc = sourceBlock.location

        if (
            sourceLoc.clone().add(1.0, 0.0, 0.0).block.type.isAir &&
            sourceLoc.clone().add(-1.0, 0.0, 0.0).block.type.isAir &&
            sourceLoc.clone().add(0.0, 0.0, 1.0).block.type.isAir &&
            sourceLoc.clone().add(0.0, 0.0, -1.0).block.type.isAir
            ) {
            sourceLoc.world.spawnFallingBlock(sourceLoc.add(0.5, 0.1, 0.5), sourceBlock.blockData)
            sourceBlock.type = Material.AIR
            checkPhysics(sourceLoc.add(0.0, 1.0, 0.0).block)
        }
    }

    private val onExplode = listen<BlockExplodeEvent>(register = false) {
        explode(it.blockList())
    }

    private val onEntityExplode = listen<EntityExplodeEvent>(register = false) {
        explode(it.blockList())
    }

    private fun explode(blocks: MutableList<Block>) {
        val pickaxe = ItemStack(Material.DIAMOND_PICKAXE)
        blocks.forEach { block ->
            val materials = when (val blockType = block.type) {
                Material.SANDSTONE -> listOf(Material.SAND)
                Material.RED_SANDSTONE -> listOf(Material.RED_SAND)

                else -> block.getDrops(pickaxe).map { it.type }
            }

            val blockLoc = block.location.add(.5, .1, .5)
            materials.forEach { material ->
                val direction = Vector(Random.nextDouble(-0.9, 0.9), Random.nextDouble(0.2, 1.0), Random.nextDouble(-0.9, 0.9))
                if (material.isBlock) {
                    val fb = block.world.spawnFallingBlock(blockLoc, Bukkit.createBlockData(material))
                    fb.velocity = direction
                } else {
                    val item = block.world.dropItem(blockLoc, ItemStack(material))
                    item.velocity = direction
                }
            }

            block.type = Material.AIR
        }
        blocks.clear()
    }
}