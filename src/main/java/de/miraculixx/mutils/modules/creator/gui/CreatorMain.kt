package de.miraculixx.mutils.modules.creator.gui

import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.modules.creator.CreatorManager
import de.miraculixx.mutils.modules.gui.GUITools
import de.miraculixx.mutils.utils.text.cHighlight
import de.miraculixx.mutils.utils.text.cmp
import de.miraculixx.mutils.utils.text.emptyComponent
import de.miraculixx.mutils.utils.text.plus
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class CreatorMain(val it: InventoryClickEvent) {
    val tool = GUITools(null)

    init {
        event()
    }

    private fun event() {
        val item = it.currentItem
        val player = it.whoClicked as Player
        when (item?.itemMeta?.customModelData ?: 0) {
            200 -> {
                GUIBuilder(player, GUI.SELECT_MENU, GUIAnimation.SPLIT).custom().open()
                player.click()
            }

            3 -> {
                val challenges = CreatorManager.getAllChallenges()
                var counter = 1
                val itemMap = buildMap {
                    challenges.forEach { challenge ->
                        put(
                            CreatorManager.modifyItem(challenge.item, counter, listOf(emptyComponent(), cmp("Left click", cHighlight) + cmp(" ≫ Toggle Active"))),
                            CreatorManager.isActive(challenge.uuid)
                        )
                        counter++
                    }
                }
                GUIBuilder(player, GUI.CREATOR_LIST, GUIAnimation.SPLIT).scroll(0, itemMap).open()
                player.click()
            }
        }
    }
}