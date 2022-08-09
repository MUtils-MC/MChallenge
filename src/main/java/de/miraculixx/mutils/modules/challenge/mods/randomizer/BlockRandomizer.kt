package de.miraculixx.mutils.modules.challenge.mods.randomizer

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.challenge.Challenge
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.worlds
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class BlockRandomizer : Challenge {
    override val challenge = Modules.RANDOMIZER_BLOCK
    private var random = true
    private val map = HashMap<Material, Material>()
    private val list = ArrayList<Material>()

    override fun start(): Boolean {
        val c = ConfigManager.getConfig(Configs.MODULES)
        val rnd = Random(worlds.first().seed)
        random = c.getBoolean("RANDOMIZER_BLOCK.Random")
        if (!random) {
            val drops = Material.values().filter { it.isItem }.shuffled(rnd)
            var block = mutableListOf<Material>()
            block.shuffle(rnd)
            drops.forEach { dropMaterial ->
                if (block.isEmpty()) {
                    block = Material.values().filter { it.isBlock }.toMutableList()
                    block.shuffle(rnd)
                }
                map[block[0]] = dropMaterial
                block.removeAt(0)
            }
        } else {
            Material.values().forEach {
                list.add(it)
            }
            list.shuffle(rnd)
        }
        return true
    }

    override fun stop() {
        map.clear()
        list.clear()
    }

    override fun register() {
        onBlockBreak.register()
        onExplode.register()
        onExplode2.register()
    }

    override fun unregister() {
        onBlockBreak.unregister()
        onExplode.unregister()
        onExplode2.unregister()
    }

    private val onBlockBreak = listen<BlockBreakEvent>(register = false) {
        it.isDropItems = false
        dropItem(it.block)
    }

    private val onExplode = listen<BlockExplodeEvent>(register = false) {
        it.blockList().forEach { block ->
            dropItem(block)
            block.type = Material.AIR
        }
        it.blockList().clear()
    }

    private val onExplode2 = listen<EntityExplodeEvent>(register = false) {
        it.blockList().forEach { block ->
            dropItem(block)
            block.type = Material.AIR
        }
        it.blockList().clear()
    }

    private fun dropItem(block: Block) {
        val material = if (random) {
            list[Random.nextInt(0, list.size - 1)]
        } else {
            val mat = block.type
            map[mat] ?: Material.STONE
        }
        val loc = block.location.add(0.5, 0.5, 0.5)
        loc.world.dropItem(loc, ItemStack(material))
    }
}