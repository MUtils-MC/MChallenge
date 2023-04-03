package de.miraculixx.mchallenge.modules.mods.tron

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.mvanilla.messages.namespace
import de.miraculixx.api.settings.challenges
import de.miraculixx.api.settings.getSetting
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.Shulker
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*

class TronPath(
    private val uuid: UUID,
    private val pathColor: Material,
) {
    private val pathVisible: Boolean
    private val blockedLocs: MutableList<Location> = mutableListOf()
    private var transitionBlock: Block? = null

    init {
        val settings = challenges.getSetting(Challenges.TRON)
        pathVisible = settings.settings["visible"]?.toBool()?.getValue() ?: true
    }

    private val onMove = listen<PlayerMoveEvent> {
        val player = it.player
        val to = it.to
        if (it.from.block == to.block) return@listen
        val subLoc = to.clone().subtract(.0, 1.0, .0)
        val subBlock = subLoc.block
        if (subBlock.type.isAir) return@listen

        // Walking on path
        if (blockedLocs.contains(subBlock.location)) {
            player.persistentDataContainer.set(NamespacedKey(namespace, "death.custom"), PersistentDataType.STRING, "tron")
            player.damage(999.9)
            subBlock.type = Material.REDSTONE_BLOCK
            val marker = subLoc.world.spawnEntity(subLoc, EntityType.SHULKER) as Shulker
            marker.isGlowing = true
            marker.setAI(false)
            marker.isInvisible = true
            marker.isSilent = true
            marker.isInvulnerable = true
        }

        // Creating new path
        if (transitionBlock != null) {
            if (pathVisible) transitionBlock!!.type = pathColor
            blockedLocs.add(transitionBlock!!.location)
        }
        if (pathVisible) subBlock.type = Material.EMERALD_BLOCK
        transitionBlock = subBlock
    }

    private val onBreak = listen<BlockBreakEvent> {
        val block = it.block
        if (block == transitionBlock || blockedLocs.contains(block.location)) it.isCancelled = true
    }

    private val onExplosion = listen<BlockExplodeEvent> {
        val block = it.block
        if (block == transitionBlock || blockedLocs.contains(block.location)) it.isCancelled = true
    }

    fun unregister() {
        onMove.unregister()
        onBreak.unregister()
        onExplosion.unregister()
    }

    fun register() {
        onMove.register()
        onBreak.register()
        onExplosion.register()
    }
}