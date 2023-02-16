package de.miraculixx.mutils.modules.mods.mobBlocks

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.api.modules.challenges.Challenges
import de.miraculixx.api.modules.challenges.Challenge
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.api.settings.challenges
import de.miraculixx.api.settings.getSetting
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ItemMergeEvent

class MobBlocks : Challenge {
    override val challenge = Challenges.MOB_BLOCKS
    private var manager: MobRandomizerData? = null

    init {
        val settings = challenges.getSetting(Challenges.MOB_BLOCKS).settings
        manager = MobRandomizerData(settings["rnd"]?.toBool()?.getValue() ?: false)
    }

    override fun start(): Boolean {

        manager?.generate()
        return true
    }

    override fun stop() {
        manager = null
    }

    override fun register() {
        onBreak.register()
        onCollect.register()
        onMerge.register()
        onExplode.register()
        onExplode2.register()
    }
    override fun unregister() {
        onBreak.unregister()
        onCollect.unregister()
        onMerge.unregister()
        onExplode.unregister()
        onExplode2.unregister()
    }

    private val onBreak = listen<BlockBreakEvent>(register = false) {
        if (Spectator.isSpectator(it.player.uniqueId)) return@listen
        manager?.spawnEntity(it.block)
        it.isDropItems = false
    }

    private val onCollect = listen<EntityPickupItemEvent>(register = false) {
        if (it.item.isInsideVehicle) it.isCancelled = true
    }

    private val onMerge = listen<ItemMergeEvent>(register = false) {
        if (it.entity.isInsideVehicle) it.isCancelled = true
    }

    private val onExplode = listen<EntityExplodeEvent>(register = false) {
        it.blockList().forEach { block -> block.type = Material.AIR }
        it.blockList().clear()
    }

    private val onExplode2 = listen<BlockExplodeEvent>(register = false) {
        it.blockList().forEach { block -> block.type = Material.AIR }
        it.blockList().clear()
    }
}