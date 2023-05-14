package de.miraculixx.mchallenge.modules.mods.chunkFlattener

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.modules.spectator.Spectator
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

class ChunkFlattener: Challenge {
    private val delay: Int
    private val shouldBreak: Boolean
    private var paused = true
    private var stop = false

    init {
        val settings = challenges.getSetting(Challenges.CHUNK_FLATTENER).settings
        delay = settings["delay"]?.toInt()?.getValue() ?: 15
        shouldBreak = settings["shouldBreak"]?.toBool()?.getValue() ?: false
    }

    override fun register() {
        paused = false
    }

    override fun unregister() {
        paused = true
    }

    override fun stop() {
        paused = true
        stop = true
    }

    override fun start(): Boolean {
        scheduler()
        return true
    }

    private fun scheduler() {
        var countdown = delay
        task(false, 0, 20) {
            if (stop) it.cancel()
            if (paused) return@task
            if (countdown == 0) {
                onlinePlayers.forEach { p ->
                    if (Spectator.isSpectator(p.uniqueId) || p.gameMode == GameMode.SPECTATOR) return@forEach
                    val world = p.world
                    val chunk = p.chunk
                    val baseX = chunk.x * 16
                    val baseZ = chunk.z * 16
                    for (x in 0..15) {
                        for (z in 0..15) {
                            val block = world.getHighestBlockAt(baseX + x, baseZ + z)
                            if (block.y <= world.minHeight + 1) return@forEach
                            sync {
                                if (shouldBreak) {
                                    val tool = ItemStack(Material.DIAMOND_PICKAXE)
                                    getHighestSolidBlock(block.x, block.y, block.z, world).breakNaturally(tool)
                                } else block.type = Material.AIR
                            }
                        }
                    }
                }
                countdown = delay
            }
            countdown--
        }
    }

    private fun getHighestSolidBlock(x: Int, y: Int, z: Int, world: World): Block {
        val block = world.getBlockAt(x, y, z)
        val type = block.type
        if (y <= world.minHeight + 1) return block
        return if (type == Material.WATER || type == Material.LAVA) {
            getHighestSolidBlock(x, y - 1, z, world)
        } else block
    }
}