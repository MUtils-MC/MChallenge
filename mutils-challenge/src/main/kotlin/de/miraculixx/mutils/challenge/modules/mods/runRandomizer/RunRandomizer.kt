package de.miraculixx.mutils.modules.challenge.mods.runRandomizer

import de.miraculixx.mutils.challenge.utils.enums.Challenge
import de.miraculixx.mutils.challenge.modules.Challenge
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent

class RunRandomizer : Challenge {
    override val challenge = Challenge.RUN_RANDOMIZER
    private var runRandomObj: RunRandomizerData? = null

    override fun start(): Boolean {
        val config = ConfigManager.getConfig(Configs.MODULES)
        runRandomObj = RunRandomizerData(config.getInt("RUN_RANDOMIZER.Goal"))
        onlinePlayers.forEach {
            runRandomObj!!.resetStats(it)
        }
        return true
    }

    override fun stop() {
        runRandomObj?.removeAll()
        runRandomObj = null
    }

    override fun register() {
        onBreak.register()
        onInteract.register()
        onInventory.register()
        onExplode.register()
        onExplodeB.register()
        onDrop.register()
        onKill.register()
        onCollect.register()
    }
    override fun unregister() {
        onBreak.unregister()
        onInteract.unregister()
        onInventory.unregister()
        onExplode.unregister()
        onExplodeB.unregister()
        onDrop.unregister()
        onKill.unregister()
        onCollect.unregister()
    }

    private val onCollect = listen<EntityPickupItemEvent>(register = false) {
        if (!it.item.scoreboardTags.contains("Dropped")) {
            it.isCancelled = true
            it.item.remove()
        }
    }

    private val onBreak = listen<BlockBreakEvent>(register = false) {
        it.isDropItems = false
    }

    private val onInteract = listen<PlayerInteractEvent>(register = false) {
        if (it.clickedBlock == null) return@listen
        if (it.clickedBlock!!.type == Material.CHEST) {
            it.clickedBlock!!.type = Material.BARREL
            it.player.playSound(it.clickedBlock!!.location, Sound.ENTITY_ITEM_PICKUP, 1f, 2F)
        }
    }

    private val onInventory = listen<InventoryOpenEvent>(register = false) {
        if (it.inventory.type == InventoryType.MERCHANT) {
            it.isCancelled = true
        }
    }

    private val onExplode = listen<EntityExplodeEvent>(register = false) {
        for (block in it.blockList()) {
            block.type = Material.AIR
        }
        it.blockList().clear()
    }
    private val onExplodeB = listen<BlockExplodeEvent>(register = false) {
        for (block in it.blockList()) {
            block.type = Material.AIR
        }
        it.blockList().clear()
    }

    private val onDrop = listen<PlayerDropItemEvent>(register = false) {
        it.itemDrop.addScoreboardTag("Dropped")
    }

    private val onKill = listen<EntityDeathEvent>(register = false) {
        it.droppedExp = 0
        it.drops.clear()
    }

}