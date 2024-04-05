package de.miraculixx.mchallenge.commands

import de.miraculixx.mchallenge.modules.competition.CompetitionManager
import de.miraculixx.mchallenge.modules.competition.CompetitionPointRule
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor

class CompetitionCommand {
    val command = commandTree("competition") {
        literalArgument("next-round") {

        }
        literalArgument("new") {
            playerExecutor { player, _ ->
                CompetitionManager.createNewCompetition(buildMap {
                    val randomOrder = CompetitionPointRule.entries.toTypedArray()
                    randomOrder.shuffle()
                    randomOrder.forEach {
                        put(it, it.defaultPoints)
                    }
                })
            }
        }
        literalArgument("leaderboard") {
            anyExecutor { _, _ ->
                CompetitionManager.printLeaderboard()
            }
        }
    }
}