package de.miraculixx.mutils.modules.challenge.mods.realistic

import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.util.Vector
import kotlin.random.Random

class BlockPhysics(event: String, block: Block) {

    init {
        when (event){
            "TREE" -> treeChopper(block)
            "BREAK" -> fallDown(block)
            "EXPLODE" -> explode(block)
        }
    }

    private fun explode(blockImport: Block) {
        when (blockImport.type){
            Material.GRASS_BLOCK -> blockImport.type = Material.DIRT
            Material.STONE -> blockImport.type = Material.COBBLESTONE
            else -> {}
        }
        val falling = blockImport.world.spawnFallingBlock(blockImport.location.clone().add(0.5,0.1,0.5),blockImport.blockData)
        falling.velocity = Vector(Random.nextDouble(-0.9,0.9),Random.nextDouble(0.2,1.0),Random.nextDouble(-0.9,0.9))
        falling.dropItem = (1..2).random() == 1
        blockImport.type = Material.AIR
    }

    private fun treeChopper(blockImport: Block) {
        var run = true
        var block = blockImport
        var loops = 0
        var internalLoops = 0
        while (run) {
            if (internalLoops > 2000) {
                //Lag protection - zu große Bäume oder Wälder, welche gebaut sind wie ein Baum
                return
            }
            if (Tag.LOGS.isTagged(block.type) || Tag.MINEABLE_HOE.isTagged(block.type) || block.type.name.contains("MUSHROOM")) {
                for (z in -3..3) {
                    for (x in -3..3) {
                        val rBlock = block.world.getBlockAt(block.location.clone().add(x + 0.0, 0.0, z + 0.0))
                        if (Tag.MINEABLE_HOE.isTagged(rBlock.type) || Tag.LOGS.isTagged(rBlock.type) || block.type.name.contains("MUSHROOM")) {
                            rBlock.world.spawnFallingBlock(rBlock.location.clone().add(0.5, 0.2, 0.5), rBlock.blockData)
                            rBlock.type = Material.AIR
                        }
                        if (rBlock == block) {
                            continue
                        }
                        if (Tag.LOGS.isTagged(rBlock.type)) {
                            if (loops > 10) continue
                            loops++
                            treeChopper(rBlock)
                        }
                        internalLoops++
                    }
                }
            }
            block = block.location.clone().add(0.0, 1.0, 0.0).block
            if (block.type == Material.AIR) run = false
        }
    }

    private fun fallDown(blockImport: Block) {
        var run = true
        var block = blockImport.location.clone().add(0.0, 1.0, 0.0).block
        while (run) {
            if (!block.type.isAir && block.type != Material.BEDROCK) {
                if (!block.location.clone().add(1.0,0.0,0.0).block.type.isAir || !block.location.clone().add(-1.0,0.0,0.0).block.type.isAir ||
                    !block.location.clone().add(0.0,0.0,1.0).block.type.isAir || !block.location.clone().add(0.0,0.0,-1.0).block.type.isAir) run = false

                val falling = block.world.spawnFallingBlock(block.location.clone().add(0.5, 0.0, 0.5), block.blockData)
                falling.dropItem = false
                block.type = Material.AIR

                block = block.location.clone().add(0.0, 1.0, 0.0).block
                continue
            }
            run = false
        }
    }
}