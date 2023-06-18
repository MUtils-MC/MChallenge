package de.miraculixx.mchallenge.modules.mods.randomizer

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.worlds
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class BlockRandomizer : Challenge {
    private var random: Boolean
    private val map: MutableMap<Material, Material> = mutableMapOf()
    private val list: MutableList<Material> = mutableListOf()

    init {
        val settings = challenges.getSetting(Challenges.RANDOMIZER_BLOCK).settings
        random = settings["random"]?.toBool()?.getValue() ?: true
    }

    override fun start(): Boolean {
        val rnd = Random(worlds.first().seed)
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
            list.addAll(Material.values())
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