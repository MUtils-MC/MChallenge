package de.miraculixx.mchallenge.modules.mods.misc.blockWall

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mvanilla.extensions.enumOf
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.player.PlayerMoveEvent

class BlockWall : Challenge {
    private val type: Material
    private val delay: Long

    private val wallBlocks: MutableSet<Block> = mutableSetOf()

    init {
        val settings = challenges.getSetting(Challenges.BLOCK_WALL).settings
        type = enumOf<Material>(settings["material"]?.toEnum()?.getValue() ?: "BEDROCK") ?: Material.BEDROCK
        delay = ((settings["delay"]?.toDouble()?.getValue() ?: 3.0) * 20).toLong()
    }

    override fun register() {
        onMove.register()
        blockToBlock.register()
        onBlockBreak.register()
    }

    override fun unregister() {
        onMove.unregister()
        blockToBlock.unregister()
        onBlockBreak.unregister()
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        val from = it.from
        val to = it.to
        val world = from.world
        if (from.block == to.block) return@listen

        taskRunLater(delay) {
            (world.minHeight + 1..world.maxHeight).forEach { y ->
                val block = from.clone().apply { setY(y.toDouble()) }.block
                if (block.type != Material.BEDROCK) {
                    block.type = type
                    wallBlocks.add(block)
                }
            }
        }
    }

    private val blockToBlock = listen<BlockFromToEvent>(register = false) {
        if (wallBlocks.contains(it.block)) it.isCancelled = true
    }

    private val onBlockBreak = listen<BlockBreakEvent>(register = false) {
        if (wallBlocks.contains(it.block)) it.isCancelled = true
    }
}