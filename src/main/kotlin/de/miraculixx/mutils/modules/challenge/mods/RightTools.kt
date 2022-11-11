package de.miraculixx.mutils.modules.challenge.mods

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.modules.challenge.Challenge
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Tag
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent

class RightTools : Challenge {
    override val challenge = Modules.RIGHT_TOOL

    override fun start(): Boolean {
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
            Tag.MINEABLE_AXE.isTagged(blockType) && itemType.endsWith("_AXE") -> true
            Tag.MINEABLE_PICKAXE.isTagged(blockType) && itemType.endsWith("_PICKAXE") -> true
            Tag.MINEABLE_SHOVEL.isTagged(blockType) && itemType.endsWith("_SHOVEL") -> true
            Tag.MINEABLE_HOE.isTagged(blockType) && itemType.endsWith("_HOE") -> true
            itemType == "SHEARS" -> Tag.WOOL.isTagged(blockType) || Tag.REPLACEABLE_PLANTS.isTagged(blockType) || Tag.FLOWERS.isTagged(blockType)
                    || Tag.LEAVES.isTagged(blockType)
            else -> false
        }
        if (!valid) {
            it.isCancelled = true
            spawnParticle(block.location)
        }
    }
}