package de.miraculixx.mchallenge.modules.mods.randomizer.runRandom

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.challenge.api.modules.mods.runRandom.RunRandomData
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.global.getSetting
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mcore.utils.getMaterials
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class RunRandomizer : Challenge {
    private val goal: Double
    private val global: Boolean
    private val items = getMaterials(false)
    private val dataMap = mutableMapOf<UUID, RunRandomData>()
    private val globalID = UUID.randomUUID()
    private val msgGoal = msgString("event.randomizer.goal")

    init {
        val settings = challenges.getSetting(Challenges.RUN_RANDOMIZER).settings
        goal = settings["goal"]?.toInt()?.getValue()?.toDouble() ?: 250.0
        global = settings["global"]?.toBool()?.getValue() ?: false
    }

    override fun start(): Boolean {
        if (global) {
            val obj = RunRandomData(0.0, BossBar.bossBar(cmp("Waiting on server...", cError), 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS))
            dataMap[globalID] = obj
        } else {
            onlinePlayers.forEach { p ->
                val uuid = p.uniqueId
                if (Spectator.isSpectator(uuid)) return@forEach
                dataMap[uuid] = RunRandomData(0.0, BossBar.bossBar(cmp("Waiting on server...", cError), 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS))
            }
        }
        return true
    }

    override fun stop() {
        if (global) {
            val bar = dataMap[globalID]?.bar ?: return
            onlinePlayers.forEach { p -> p.hideBossBar(bar) }
        } else onlinePlayers.forEach { p -> dataMap[p.uniqueId]?.bar?.let { p.hideBossBar(it) } }
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
        if (distance <= 0) {

            return@listen
        }
        val player = it.player
        update(player, distance)
    }

    private fun update(player: Player, distance: Double) {
        val data = if (global) {
            val d = dataMap[globalID]!!
            onlinePlayers.forEach { p -> p.showBossBar(d.bar) }
            d
        } else {
            val d = dataMap.getOrPut(player.uniqueId) { RunRandomData(0.0, BossBar.bossBar(cmp("Waiting on server...", cError), 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS)) }
            player.showBossBar(d.bar)
            d
        }

        val prev = data.distance
        data.distance += distance
        if (prev >= goal) return //Prevent visual updates on flashing

        val bar = data.bar
        if (data.distance >= goal) {
            val goalString = goal.toInt().toString()
            bar.progress(1f)
            bar.color(BossBar.Color.GREEN)
            bar.name(cmp("$msgGoal: $goalString/$goalString", cSuccess))
            if (global) {
                onlinePlayers.forEach { p ->
                    p.inventory.addItem(ItemStack(items.random(), 6))
                    p.playSound(p, Sound.ENTITY_PUFFER_FISH_BLOW_UP, 1f, 1.2f)
                }
            } else {
                player.inventory.addItem(ItemStack(items.random(), 64))
                player.playSound(player, Sound.ENTITY_PUFFER_FISH_BLOW_UP, 1f, 1.2f)
            }
            taskRunLater(30) { data.distance -= goal }
        } else {
            bar.progress((data.distance / goal).toFloat())
            bar.color(BossBar.Color.YELLOW)
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