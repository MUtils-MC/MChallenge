package de.miraculixx.mchallenge.modules.mods.simple.rightTools

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.meta.Damageable

class RightTools : Challenge {
    private val starterAxe: Boolean

    init {
        val settings = challenges.getSetting(Challenges.RIGHT_TOOL).settings
        starterAxe = settings["starter"]?.toBool()?.getValue() ?: true
    }

    override fun start(): Boolean {
        if (starterAxe) {
            val axe = itemStack(Material.WOODEN_AXE) {
                meta<Damageable> {
                    damage = maxItemUseDuration / 2
                }
            }
            onlinePlayers.forEach { player ->
                if (player.gameMode == GameMode.SURVIVAL)
                    player.inventory.addItem(axe)
            }
        }
        return true
    }

    override fun stop() {}

    override fun register() {
        onBlockBreak.register()
        onDamage.register()
    }

    override fun unregister() {
        onBlockBreak.unregister()
        onDamage.unregister()
    }

    private fun spawnParticle(loc: Location) {
        loc.world.spawnParticle(Particle.SMOKE_NORMAL, loc.clone().add(0.5, 1.0, 0.5), 10, 0.1, 0.1, 0.1, 0.01, null, true)
    }

    private val onDamage = listen<EntityDamageByEntityEvent>(register = false) {
        if (it.damager !is Player) return@listen
        val p = it.damager as Player
        val item = p.inventory.itemInMainHand
        val target = it.entity
        if (!item.type.name.endsWith("_SWORD")) {
            it.isCancelled = true
            spawnParticle(target.location)
        }
    }

    private val onBlockBreak = listen<BlockBreakEvent>(register = false) {
        val item = it.player.inventory.itemInMainHand
        val itemType = item.type.name
        val block = it.block
        val blockType = block.type
        val valid = when {
            (Tag.MINEABLE_AXE.isTagged(blockType) || Tag.BEDS.isTagged(blockType)) && itemType.endsWith("_AXE") -> true
            Tag.MINEABLE_PICKAXE.isTagged(blockType) && itemType.endsWith("_PICKAXE") -> true
            Tag.MINEABLE_SHOVEL.isTagged(blockType) && itemType.endsWith("_SHOVEL") -> true
            Tag.MINEABLE_HOE.isTagged(blockType) && itemType.endsWith("_HOE") -> true
            itemType == "SHEARS" -> Tag.WOOL.isTagged(blockType) || Tag.REPLACEABLE.isTagged(blockType) || Tag.FLOWERS.isTagged(blockType)
                    || Tag.LEAVES.isTagged(blockType)

            blockType.name.endsWith("TORCH") || blockType == Material.SEA_PICKLE -> true
            else -> false
        }
        val dropBlocks = block.getDrops(item, it.player).isNotEmpty()
        if (!valid || !dropBlocks) {
            it.isCancelled = true
            spawnParticle(block.location)
        }
    }
}