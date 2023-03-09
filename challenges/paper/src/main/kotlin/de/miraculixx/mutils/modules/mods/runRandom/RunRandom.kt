package de.miraculixx.mutils.modules.challenge.mods.runRandomizer

import de.miraculixx.mutils.utils.enums.Challenge
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
    private val goal: Int
    private val global: Boolean
    private val items = GET ITEMS HERE
    private val dataMap = mutableMapOf<UUID, RunRandomData>()
    private val globalID = UUID.randomUUID()
    private val msgGoal = msgString("event.randomizer.goal")

    init {
        val settings = challenges.getSettings(challenge).settings
        goal = settings["goal"]?.toInt()?.getValue()? ?: 250.0
        global = settings["global"]?.toBool()?.getValue() ?: false
    }

    override fun start(): Boolean {
        if (global) {
            val obj = RunRandomizerData(0, BossBar.bar(cmp("Waiting on server...", cError), 1.0, SOLID))
            dataMap[globalID] = obj
        } else {
            onlinePlayers.forEach { p ->
                val uuid = p.uniqueID
                if (Spectator.isSpectator(uuid)) return@forEach
                dataMap[uuid] = RunRandomizerData(0, BossBar.bar(cmp("Waiting on server...", cError), 1.0, SOLID))
            }
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
        onMove.register()
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
        onMove.unregister()
    }

    private val onMove = listen<PlayerMoveEvent>(register = false) {
        val distance = it.from.distance(it.to)
        val uuid = it.player.uniqueID

    }

    private fun update(player: Player, distance: Double) {
        val data = if (global) {
            val d = dataMap[globalID]!!
            onlinePlayers.forEach { p -> p.showBar(d.bar) }
            d
        } else {
            val d = dataMap.getOrSet(player.uniqueID) { RunRandomizerData(0, BossBar.bar(cmp("Waiting on server...", cError), 1.0, SOLID)) }
            player.showBar(d.bar)
            d
        }
        
        val prev = data.distance
        data.distance += distance
        if (prev >= goal) return //Prevent visual updates on flashing

        val bar = data.bar
        if (data.distance >= goal) {
            val goalString = goal.toInt().toString()
            bar.progress(1f)
            bar.color(GREEN)
            bar.name(cmp("$msgGoal: $goalString/$goalString"))
            if (global) {
                onlinePlayers.forEach { p -> 
                    p.inventory.addItem(ItemStack(items.random(), 6)
                    //Sound
                }
            } else {
                player.inventory.addItem(ItemStack(items.random(), 64))
                //Sound
            }
            runTaskLater(30) { data.distance -= goal }
        } else {
            bar.progress(goal / data.distance)
            bar.color(YELLOW)
            bar.name(cmp("$msgGoal: ") + cmp(data.distance.toInt().toString(), cSuccess) + cmp("/") + cmp(goal.toInt().toString(), cError))
        }
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
        val block = it.clickedBlock ?: return@listen
        if (block.type == Material.CHEST) {
            block.type = Material.BARREL
            it.player.playSound(block.location, Sound.ENTITY_ITEM_PICKUP, 1f, 2F)
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