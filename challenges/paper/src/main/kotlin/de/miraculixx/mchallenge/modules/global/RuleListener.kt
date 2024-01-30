package de.miraculixx.mchallenge.modules.global

import de.miraculixx.kpaper.event.listen
import de.miraculixx.mvanilla.messages.cmp
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.RenderType

object RuleListener {
    private val rules: MutableMap<CustomRules, Boolean> = mutableMapOf()
    private val sb = Bukkit.getScoreboardManager().mainScoreboard
    private val objectiveHIT = sb.getObjective("mutils-hearts-in-tab") ?: sb.registerNewObjective("mutils-hearts-in-tab", Criteria.HEALTH, cmp("HP"), RenderType.HEARTS)

    fun updateRule(rule: CustomRules, active: Boolean) {
        rules[rule] = active
        when (rule) {
            CustomRules.HEARTS_IN_TAB -> objectiveHIT.displaySlot = if (active) DisplaySlot.PLAYER_LIST else null
            else -> Unit
        }
    }


    @Suppress("unused")
    private val onPvP = listen<EntityDamageByEntityEvent> {
        if (it.entity is Player && it.damager is Player && rules[CustomRules.PVP] == false)
            it.isCancelled = true
    }
}

enum class CustomRules {
    HEARTS_IN_TAB,
    PVP,
    FANCY_SYSTEM_MESSAGES
}