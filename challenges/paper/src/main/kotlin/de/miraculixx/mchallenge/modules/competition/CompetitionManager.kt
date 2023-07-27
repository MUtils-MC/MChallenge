@file:Suppress("unused")

package de.miraculixx.mchallenge.modules.competition

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

object CompetitionManager {
    private val playerData: MutableMap<UUID, CompetitionPlayerData> = mutableMapOf()
    private var tasks: Map<CompetitionPointRule, Int> = emptyMap()
    private var ranks: Set<UUID> = mutableSetOf()
    private val mapRenderer = MapManager(MChallenge.configFolder)

    fun createNewCompetition(tasks: Map<CompetitionPointRule, Int>) {
        this.tasks = tasks.toSortedMap { key1: CompetitionPointRule, key2: CompetitionPointRule ->
            when {
                key1.sortOrder > key2.sortOrder -> 1
                key1.sortOrder < key2.sortOrder -> -1
                else -> 0
            }
        }
        playerData.clear()
        updateRanks()
        updateItem()
    }

    private fun updateItem() {
        val top3 = getTop3()
        onlinePlayers.forEach { player ->
            updateItem(player, top3)
        }
    }

    fun printLeaderboard() {
        broadcast(prefix + cmp("Leadboard aktueller Runde:", cMark))
        ranks.forEachIndexed { index, uuid ->
            val display = when (index) {
                0 -> cmp("①", cError, bold = true)
                1 -> cmp("②", NamedTextColor.GOLD, bold = true)
                2 -> cmp("③", NamedTextColor.YELLOW, bold = true)
                else -> cmp("$index", NamedTextColor.GRAY)
            }
            val playerData = playerData[uuid]
            broadcast(display + cmp((" " + (Bukkit.getOfflinePlayer(uuid).name ?: "Unknown")), NamedTextColor.WHITE) + cmp(" (${playerData?.points ?: 0})"))
        }
    }

    private fun updateItem(player: Player, top3: Map<UUID, CompetitionPlayerData?>) {
        val uuid = player.uniqueId
        val data = playerData.getOrPut(uuid) {
            val mapItem = mapRenderer.getMapItem(uuid, emptyMap())
            player.inventory.setItemInOffHand(mapItem)
            CompetitionPlayerData(0, mutableSetOf(), tasks.toMutableMap(), mapItem)
        }
        mapRenderer.requestUpdate(uuid, data, ranks.indexOfFirst { it == uuid }, top3)
    }

    private fun updateRanks() {
        val allPlayers = (onlinePlayers.map { it.uniqueId } + playerData.keys).toSet()
        val sorted = allPlayers.sortedByDescending { uuid ->
            playerData[uuid]?.points ?: 0
        }
        ranks = sorted.toSet()
    }

    private fun getTop3(): Map<UUID, CompetitionPlayerData?> {
        return buildMap {
            val topRanks = ranks.toList()
            topRanks.getOrNull(0)?.let { put(it, playerData[it]) }
            topRanks.getOrNull(1)?.let { put(it, playerData[it]) }
            topRanks.getOrNull(2)?.let { put(it, playerData[it]) }
        }
    }

    private fun Player.finishTask(task: CompetitionPointRule) {
        if (gameMode != GameMode.SURVIVAL) return
        val data = playerData[uniqueId] ?: return
        if (data.remainingTasks.remove(task) == null) return
        addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 10, 1, false, false, false))
        playSound(this, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.2f, 2f)
        sendMessage(prefix + cmp("Aufgabe ") + cmp(task.display, cMark) + cmp(" abgeschlossen! ") + cmp("+${task.defaultPoints} Punkte", cSuccess))
        data.finishedTasks.add(task)
        data.points += task.defaultPoints
        updateRanks()
        updateItem()
    }


    //
    // Events
    //

    private val onAdv = listen<PlayerAdvancementDoneEvent> {
        if (it.player.gameMode != GameMode.SURVIVAL) it.message(null)
    }

    private val onJoin = listen<PlayerJoinEvent> {
        updateRanks()
        updateItem()
    }

    private val onInteract = listen<PlayerInteractEvent> {
        if (it.hand == EquipmentSlot.HAND) return@listen
        if (!it.action.isRightClick) return@listen
        val player = it.player
        val playerData = playerData[player.uniqueId] ?: return@listen
        playerData.mapView = when (playerData.mapView) {
            CompetitionMapView.LEADERBOARD -> CompetitionMapView.PERSONAL_TASKS
            CompetitionMapView.PERSONAL_TASKS -> CompetitionMapView.LEADERBOARD
        }
        updateItem(player, getTop3())
    }

    private val onInvClick = listen<InventoryClickEvent> {
        val item = it.currentItem ?: return@listen
        (it.whoClicked as? Player)?.collectItem(item.type)
        val meta = item.itemMeta ?: return@listen
        if (meta.customModel == 1200) it.isCancelled = true
    }

    private val onDrop = listen<PlayerDropItemEvent> {
        val item = it.itemDrop.itemStack
        val meta = item.itemMeta ?: return@listen
        if (meta.customModel == 1200) it.isCancelled = true
    }

    private val onPlayerDeath = listen<PlayerDeathEvent> {
        it.itemsToKeep.addAll(it.player.inventory.filter { item -> item.itemMeta?.customModel == 1200 })
    }

    private val onCollect = listen<EntityPickupItemEvent> {
        val player = it.entity as? Player ?: return@listen
        player.collectItem(it.item.itemStack.type)
    }

    private val onEntityDeath = listen<EntityDamageByEntityEvent> {
        val player = it.damager as? Player ?: return@listen
        val target = it.entity
        when (target.type) {
            EntityType.ENDER_DRAGON -> player.finishTask(CompetitionPointRule.KILL_DRAGON)
            EntityType.SNOWMAN -> player.finishTask(CompetitionPointRule.KILL_SNOWMAN)
            else -> Unit
        }
    }

    private val onDimSwap = listen<PlayerChangedWorldEvent> {
        when (it.player.world.environment) {
            World.Environment.THE_END -> it.player.finishTask(CompetitionPointRule.ENTER_END)
            World.Environment.NETHER -> it.player.finishTask(CompetitionPointRule.FIRST_NETHER_IN)
            else -> Unit
        }
    }

    private fun Player.collectItem(type: Material) {
        when (type) {
            Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_SHOVEL, Material.IRON_SWORD -> finishTask(CompetitionPointRule.FIRST_IRON_TOOL)
            Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD -> finishTask(CompetitionPointRule.FIRST_GOLD_TOOL)
            Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD -> finishTask(CompetitionPointRule.FIRST_DIAMOND_TOOL)
            Material.NETHERITE_PICKAXE, Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD -> finishTask(CompetitionPointRule.FIRST_NETHERITE_TOOL)
            Material.ELYTRA -> finishTask(CompetitionPointRule.ELYTRA)
            Material.ENDER_EYE -> finishTask(CompetitionPointRule.FIRST_ENDER_EYE)
            else -> Unit
        }
    }
}