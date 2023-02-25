package de.miraculixx.mutils.modules.mods.chunkDecay

import de.miraculixx.api.modules.challenges.Challenge
import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.api.settings.challenges
import de.miraculixx.api.settings.getSetting
import de.miraculixx.kpaper.extensions.bukkit.allBlocks
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.sync
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mutils.modules.spectator.Spectator
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ChunkDecay : Challenge {
    override val challenge: Challenges = Challenges.CHUNK_DECAY
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
        task(false, 0, 20) {
            if (stop) it.cancel()
            if (paused) return@task
            if (countdown <= 0) {
                onlinePlayers.forEach { p ->
                    if (Spectator.isSpectator(p.uniqueId) || p.gameMode == GameMode.SPECTATOR) return@forEach
                    val chunk = p.chunk
                    val blocks = chunk.allBlocks.filter { b ->
                        val isFlowing = if (b.type == Material.WATER || b.type == Material.LAVA) {
                            //There should be a way to get the nbt value directly?
                            !b.blockData.asString.contains("[level=0]")
                        } else false
                        !b.type.isAir && !isFlowing
                    }
                    val tool = ItemStack(Material.DIAMOND_PICKAXE)
                    println((blocks.size * (percentage / 100.0)).toInt())
                    blocks.shuffled()
                        .chunked((blocks.size * (percentage / 100.0)).toInt().coerceAtLeast(1))
                        .firstOrNull()
                        ?.forEach { b ->
                            sync {
                                if (shouldBreak) b.breakNaturally(tool)
                                else b.type = Material.AIR
                            }
                        }
                }
                countdown = delay
            }
            countdown--
        }
    }
}