package de.miraculixx.mutils.modules.challenge.mods

import de.miraculixx.mutils.utils.enums.Challenge
import de.miraculixx.mutils.challenge.modules.Challenge
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

class ChunkBlockBreaker : Challenge {
    override val challenge = Challenge.CHUNK_BLOCK_BREAK
    private var bundle: Boolean? = null

    override fun start(): Boolean {
        val c = ConfigManager.getConfig(Configs.MODULES)
        bundle = c.getBoolean("CHUNK_BLOCK_BREAK.Bundle")
        return true
    }

    override fun stop() {}

    override fun register() {
        onBlockBreak.register()
    }

    override fun unregister() {
        onBlockBreak.unregister()
    }

    private val onBlockBreak = listen<BlockBreakEvent>(register = false) {
        val block = it.block
        val world = block.world
        val tool =  it.player.inventory.itemInMainHand
        val type = block.type
        val drops = if (tool.enchantments.containsKey(Enchantment.SILK_TOUCH)) listOf(ItemStack(type,1))
        else it.block.getDrops(tool)
        val chunk = block.chunk
        val worldMin = world.minHeight * -1
        var counter = 0

        repeat(16) { x ->
            repeat(16) { z ->
                val maxHigh = world.getHighestBlockAt(chunk.getBlock(x, 0, z).location).location.blockY
                repeat(maxHigh + worldMin + 1) { y ->
                    val b = chunk.getBlock(x, y - worldMin, z)
                    if (b.type == type) {
                        if (bundle == true) b.type = Material.AIR
                        else b.breakNaturally(tool)
                        counter++
                    }
                }
            }
        }

        if (bundle == true) {
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