package de.miraculixx.mchallenge.gui.items

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.items.itemStack
import de.miraculixx.kpaper.items.meta
import de.miraculixx.kpaper.items.name
import de.miraculixx.mchallenge.modules.challenges.Challenges
import de.miraculixx.mchallenge.modules.competition.CompetitionPointRule
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mvanilla.extensions.msg
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemsCompetitionCreator : ItemProvider {
    private val msgSetting = listOf(emptyComponent(), cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))

    private val msgRounds = msgString("items.competition.rounds.n")
    private val msgPoints = msgString("items.competition.points.n")
    private val msgChallenges = msgString("items.competition.challenges.n")

    val rounds = 3
    val pointRules: MutableSet<CompetitionPointRule> = mutableSetOf()
    val challenges: MutableList<Challenges> = mutableListOf()
    var kickOnDeath = false
    var scoreboard = true

    override fun getSlotMap(): Map<Int, ItemStack> {
        return buildMap {
            put(10, itemStack(Material.HEART_OF_THE_SEA) {
                meta {
                    name = cmp(msgRounds)
                    lore(msgList("items.competition.rounds.l") + msgSetting + listOf(cmp("   $msgRounds: ") + cmp("$rounds", cHighlight)))
                    customModel = 1
                }
            })
            put(11, itemStack(Material.REDSTONE) {
                meta {
                    name = cmp(msgPoints)
                    lore(msgList("items.competition.points.l") + msgSetting + listOf(cmp("   $msgPoints: ") + cmp("${pointRules.size} Rules", cHighlight)))
                    customModel = 2
                }
            })
            put(12, itemStack(Material.CHEST) {
                meta {
                    name = cmp(msgPoints)
                    lore(msgList("items.competition.challenges.l") + msgSetting + listOf(cmp("   $msgChallenges: ") + cmp("${challenges.size}/$rounds", cHighlight)))
                    customModel = 3
                }
            })

            put(15, itemStack(Material.SKELETON_SKULL) {
                meta {
                    name = cmp(msgString("items.competition.kickOnDeath"))
                    lore(msgList("items.competition.kickOnDeath.l"))
                    customModel = 4
                }
            })
            put(24, itemStack(if (kickOnDeath) Material.LIME_STAINED_GLASS_PANE else Material.RED_STAINED_GLASS_PANE) {
                meta {
                    name = cmp(kickOnDeath.msg(), if (kickOnDeath) NamedTextColor.GREEN else cError)
                    customModel = 4
                }
            })
        }
    }
}