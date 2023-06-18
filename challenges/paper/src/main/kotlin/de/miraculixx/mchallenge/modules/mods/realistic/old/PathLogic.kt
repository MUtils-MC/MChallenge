package de.miraculixx.mchallenge.modules.mods.realistic.old

import org.bukkit.Material
import org.bukkit.block.Block

class PathLogic {
    private val blockList = HashMap<Block, Int>()
    private var lastBlock: Block? = null

    fun addBlock(block: Block) {
        if (lastBlock == block) return
        val current = blockList.getOrPut(block) { 0 }
        blockList[block] = current + 1

        lastBlock = block

        when (current) {
            3 -> block.type = Material.COARSE_DIRT
            7 -> block.type = Material.DIRT
            13 -> {
                block.type = Material.DIRT_PATH
                blockList.remove(block)
            }
        }
    }
}
