package de.miraculixx.mutils.modules.challenge.mods.mobRandomizer

import de.miraculixx.mutils.utils.enums.Challenge
import de.miraculixx.mutils.challenge.modules.Challenge
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ItemMergeEvent

class MobRandomizer : Challenge {
    override val challenge = Challenge.MOB_RANDOMIZER
    private var manager: MobRandomizerData? = null

    override fun start(): Boolean {
        val config = ConfigManager.getConfig(Configs.MODULES)
        manager = MobRandomizerData(config.getBoolean("MOB_RANDOMIZER.Random"))
        manager!!.generate()
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
        for (block in it.blockList()) {
            block.type = Material.AIR
        }
        it.blockList().clear()
    }

    private val onExplode2 = listen<BlockExplodeEvent>(register = false) {
        for (block in it.blockList()) {
            block.type = Material.AIR
        }
        it.blockList().clear()
    }
}