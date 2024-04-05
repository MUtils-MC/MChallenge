package de.miraculixx.mchallenge.modules.competition

import org.bukkit.inventory.ItemStack

data class CompetitionPlayerData(
    var points: Int,
    var finishedTasks: MutableSet<CompetitionPointRule>,
    var remainingTasks: MutableMap<CompetitionPointRule, Int>,
    val mapItem: ItemStack,
    var mapView: CompetitionMapView = CompetitionMapView.PERSONAL_TASKS
)


enum class CompetitionMapView {
    LEADERBOARD, PERSONAL_TASKS
}