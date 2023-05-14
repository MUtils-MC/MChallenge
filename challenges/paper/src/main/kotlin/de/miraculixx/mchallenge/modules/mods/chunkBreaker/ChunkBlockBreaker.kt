package de.miraculixx.mchallenge.modules.mods.chunkBreaker

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.bukkit.allBlocks
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

class ChunkBlockBreaker : Challenge {
    private val bundle: Boolean

    init {
        val settings = challenges.getSetting(Challenges.CHUNK_BLOCK_BREAK).settings
        bundle = settings["bundle"]?.toBool()?.getValue() ?: true
    }

    override fun register() {
        onBlockBreak.register()
    }

    override fun unregister() {
        onBlockBreak.unregister()
    }

    private val onBlockBreak = listen<BlockBreakEvent>(register = false) {
        val block = it.block
        val world = block.world
        val tool = it.player.inventory.itemInMainHand
        val type = block.type
        val drops = it.block.getDrops(tool, it.player)
        val chunk = block.chunk
        var counter = 0

        chunk.allBlocks.forEach { b ->
            if (b.type != type) return@forEach
            if (bundle) b.type = Material.AIR
            else b.breakNaturally(tool)
            counter++
        }

        if (bundle) {
            val repeats = counter / 64
            val lastStack = counter % 64
            drops.forEach { drop ->
                if (!drop.type.isAir && lastStack != 0) {
                    repeat(repeats) {
                        world.dropItem(block.location, ItemStack(drop.type, 64))
                    }
                    world.dropItem(block.location, ItemStack(drop.type, lastStack))
                }
            }
        }
    }
}