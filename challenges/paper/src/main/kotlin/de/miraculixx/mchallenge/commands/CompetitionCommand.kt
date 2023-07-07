package de.miraculixx.mchallenge.commands

import de.miraculixx.mchallenge.modules.competition.CompetitionManager
import de.miraculixx.mchallenge.modules.competition.CompetitionPointRule
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor

class CompetitionCommand {
    val command = commandTree("competition") {
        playerExecutor { player, _ ->
//            GUITypes.COMPETITION.buildInventory(player, "COMPETITION", )


            CompetitionManager.createNewCompetition(buildMap {
                val randomOrder = CompetitionPointRule.values()
                randomOrder.shuffle()
                randomOrder.forEach {
                    put(it, 10)
                }
            })
        }
    }
}