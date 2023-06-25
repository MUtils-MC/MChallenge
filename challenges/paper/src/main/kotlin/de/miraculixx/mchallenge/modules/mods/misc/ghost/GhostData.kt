package de.miraculixx.mchallenge.modules.mods.misc.ghost

import de.miraculixx.kpaper.extensions.geometry.add
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player

class GhostData {
    private val currentBlocks = HashMap<Player, List<Block>>()
    private val ghostPlayer = ArrayList<Player>()
    private val radius: Int
    private val mode: Boolean

    init {
        val settings = challenges.getSetting(Challenges.GHOST).settings
        radius = settings["radius"]?.toInt()?.getValue() ?: 7
        mode = settings["glide"]?.toBool()?.getValue() ?: true

        scheduler()
    }

    /**
     * Update surrounding blocks - Cube format
     */
    fun updateCUBE(player: Player, current: Material) {
        val loc = player.location
        val oldBlocks = ArrayList<Block>(currentBlocks.getOrDefault(player, ArrayList()))
        val newBlocks = ArrayList<Block>()
        for (x in -radius..radius) {
            for (y in -radius..radius) {
                for (z in -radius..radius) {
                    val block = loc.clone().add(x, y, z).block
                    if (block.type == current) {
                        if (mode) block.type = Material.SCAFFOLDING
                        if (!mode) block.type = Material.STRUCTURE_VOID
                        newBlocks.add(block)
                        oldBlocks.remove(block)
                    } else if (block.type == Material.SCAFFOLDING || block.type == Material.STRUCTURE_VOID) {
                        newBlocks.add(block)
                        oldBlocks.remove(block)
                    }
                }
            }
        }

        oldBlocks.forEach { block ->
            block.type = current
        }
        currentBlocks[player] = newBlocks
    }

    /**
     * Update surrounding blocks - Sphere format
     */
    fun update(player: Player, current: Material) {
        val loc = player.location
        val oldBlocks = ArrayList<Block>(currentBlocks.getOrDefault(player, ArrayList()))
        val newBlocks = ArrayList<Block>()

        for (x in -radius..radius) {
            for (y in -radius..radius) {
                for (z in -radius..radius) {
                    val block = loc.block.getRelative(x, y, z)
                    if (block.location.distanceSquared(loc) >= radius * radius) continue
                    if (block.type == current) {
                        if (mode) block.type = Material.SCAFFOLDING
                        if (!mode) block.type = Material.STRUCTURE_VOID
                        newBlocks.add(block)
                        oldBlocks.remove(block)
                    } else if (block.type == Material.SCAFFOLDING || block.type == Material.STRUCTURE_VOID) {
                        newBlocks.add(block)
                        oldBlocks.remove(block)
                    }
                }
            }
        }


        oldBlocks.forEach { block ->
            block.type = current
        }
        currentBlocks[player] = newBlocks
    }

    fun reset(player: Player, current: Material) {
        currentBlocks[player]?.forEach { block -> block.type = current }
        currentBlocks.remove(player)
    }

    fun addPlayer(player: Player) {
        if (!ghostPlayer.contains(player)) ghostPlayer.add(player)
    }

    fun removePlayer(player: Player) {
        if (ghostPlayer.contains(player)) ghostPlayer.remove(player)
    }

    private fun scheduler() {
        task(true, 1, 1) {
            ghostPlayer.forEach { player -> player.freezeTicks = player.maxFreezeTicks }
        }
    }
}