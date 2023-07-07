package de.miraculixx.mchallenge.modules.competition

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mvanilla.extensions.add
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.util.UUID

object CompetitionManager {
    private val playerData: MutableMap<UUID, CompetitionPlayerData> = mutableMapOf()
    private var tasks: Map<CompetitionPointRule, Int> = emptyMap()
    private var ranks: Set<UUID> = mutableSetOf()
    private val mapRenderer = MapManager(MChallenge.configFolder)

    fun createNewCompetition(tasks: Map<CompetitionPointRule, Int>) {
        println(tasks.keys.map { it.sortOrder }.toString())
        this.tasks = tasks.toSortedMap { key1: CompetitionPointRule, key2: CompetitionPointRule ->
            when {
                key1.sortOrder > key2.sortOrder -> 1
                key1.sortOrder < key2.sortOrder -> -1
                else -> 0
            }
        }
        println(this.tasks.keys.map { it.sortOrder }.toString())
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
        allPlayers.sortedBy { uuid ->
            playerData[uuid]?.points ?: 0
        }
        ranks = allPlayers
    }

    private fun getTop3(): Map<UUID, CompetitionPlayerData?> {
        return buildMap {
            val topRanks = ranks.toList()
            topRanks.getOrNull(0)?.let { put(it, playerData[it]) }
            topRanks.getOrNull(1)?.let { put(it, playerData[it]) }
            topRanks.getOrNull(2)?.let { put(it, playerData[it]) }
        }
    }

    private val onInteract = listen<PlayerInteractEvent> {
        if (it.action != Action.RIGHT_CLICK_AIR) return@listen
        val player = it.player
        val playerData = playerData[player.uniqueId] ?: return@listen
        playerData.mapView = when (playerData.mapView) {
            CompetitionMapView.LEADERBOARD -> CompetitionMapView.PERSONAL_TASKS
            CompetitionMapView.PERSONAL_TASKS -> CompetitionMapView.LEADERBOARD
        }
        updateItem(player, getTop3())
    }
}