package de.miraculixx.mchallenge.modules.mods.worldChanging.chunkDecay

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.extensions.bukkit.allBlocks
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import de.miraculixx.mchallenge.modules.spectator.Spectator
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.data.Levelled
import org.bukkit.inventory.ItemStack

class ChunkDecay : Challenge {
    private val delay: Int
    private val percentage: Int
    private val shouldBreak: Boolean
    private var paused = true
    private var stop = false

    init {
        val settings = challenges.getSetting(Challenges.CHUNK_DECAY).settings
        delay = settings["delay"]?.toInt()?.getValue() ?: 15
        percentage = settings["percentage"]?.toInt()?.getValue() ?: 2
        shouldBreak = settings["shouldBreak"]?.toBool()?.getValue() ?: false
    }

    override fun register() {
        paused = false
    }

    override fun unregister() {
        paused = true
    }

    override fun start(): Boolean {
        scheduler()
        return true
    }

    override fun stop() {
        paused = true
        stop = true
    }

    private fun scheduler() {
        var countdown = delay
        task(true, 0, 20) {
            if (stop) it.cancel()
            if (paused) return@task
            if (countdown <= 0) {
                onlinePlayers.forEach { p ->
                    if (Spectator.isSpectator(p.uniqueId) || p.gameMode == GameMode.SPECTATOR) return@forEach
                    val chunk = p.chunk
                    val blocks = chunk.allBlocks.filter { b ->
                        val isFlowing = if (b.type == Material.WATER || b.type == Material.LAVA) {
                            (b.blockData as? Levelled)?.level == 0
                        } else false
                        !b.type.isAir && !isFlowing
                    }

                    val physics = chunk.world.environment == World.Environment.NORMAL
                    blocks.shuffled()
                        .subList(0, (blocks.size * (percentage / 100.0)).toInt().coerceAtLeast(1))
                        .forEach { b ->
                            if (shouldBreak) b.breakNaturally(ItemStack(Material.DIAMOND_PICKAXE))
                            else b.setType(Material.AIR, physics)
                        }
                }
                countdown = delay
            }
            countdown--
        }
    }
}