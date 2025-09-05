package de.miraculixx.mchallenge.modules.mods.randomizer

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.worlds
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.challenges.getSetting
import net.minecraft.world.entity.item.PrimedTnt
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.random.Random

class BlockRandomizer : Challenge {
    private var random: Boolean
    private var perPlayer: Boolean

    private val map: MutableMap<UUID, Map<Material, Material>> = mutableMapOf()
    private val list: MutableList<Material> = mutableListOf()
    private val globalKey = UUID(0, 0)

    init {
        val settings = challenges.getSetting(Challenges.RANDOMIZER_BLOCK).settings
        random = settings["random"]?.toBool()?.getValue() ?: true
        perPlayer = settings["perPlayer"]?.toBool()?.getValue() ?: false
    }

    override fun start(): Boolean {
        val seed = worlds.first().seed
        if (!random) {
            val drops = Material.entries.filter { it.isItem }
            map[globalKey] = getShuffledMap(seed, globalKey, drops)
            if (perPlayer) {
                onlinePlayers.forEach { p ->
                    map[p.uniqueId] = getShuffledMap(seed, p.uniqueId, drops)
                }
            }

        } else {
            list.addAll(Material.entries.toTypedArray())
            list.shuffle(Random(seed))
        }
        return true
    }

    private fun getShuffledMap(seed: Long, entropy: UUID, fullList: List<Material>): Map<Material, Material> {
        val rnd = Random(seed + entropy.mostSignificantBits)
        var block = mutableListOf<Material>()
        return buildMap {
            fullList.forEach { dropMaterial ->
                if (block.isEmpty()) {
                    block = fullList.shuffled(rnd).toMutableList() // copy new
                }
                put(block[0], dropMaterial)
                block.removeAt(0)
            }
        }
    }

    override fun stop() {
        map.clear()
        list.clear()
    }

    override fun register() {
        onBlockBreak.register()
        onExplode.register()
        onExplode2.register()
        onJoin.register()
    }

    override fun unregister() {
        onBlockBreak.unregister()
        onExplode.unregister()
        onExplode2.unregister()
        onJoin.unregister()
    }

    private val onBlockBreak = listen<BlockBreakEvent>(register = false) {
        it.isDropItems = false
        dropItem(it.block, it.player.uniqueId)
    }

    private val onExplode = listen<BlockExplodeEvent>(register = false) {
        it.blockList().forEach { block ->
            dropItem(block, null)
            block.type = Material.AIR
        }
        it.blockList().clear()
    }

    private val onExplode2 = listen<EntityExplodeEvent>(register = false) {
        var source = (it.entity as? TNTPrimed)?.let { e -> (e.source as? Player)?.uniqueId }

        it.blockList().forEach { block ->
            dropItem(block, source)
            block.type = Material.AIR
        }
        it.blockList().clear()
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        if (perPlayer && !random) {
            val seed = worlds.first().seed
            val uuid = it.player.uniqueId
            map[uuid] = getShuffledMap(seed, uuid, Material.entries.filter { m -> m.isItem })
        }
    }

    private fun dropItem(block: Block, source: UUID?) {
        val material = if (random) {
            list[Random.nextInt(0, list.size - 1)]
        } else {
            val mat = block.type
            if (perPlayer && source != null) {
                map[source]?.get(mat) ?: map[globalKey]?.get(mat) ?: Material.STONE
            } else {
                map[globalKey]?.get(mat) ?: Material.STONE
            }
        }
        val loc = block.location.add(0.5, 0.5, 0.5)
        loc.world.dropItem(loc, ItemStack(material))
    }
}