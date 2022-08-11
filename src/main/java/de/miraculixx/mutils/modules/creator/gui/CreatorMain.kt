package de.miraculixx.mutils.modules.creator.gui

import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.modules.creator.tools.CreatorInvTools
import de.miraculixx.mutils.modules.gui.GUITools
import de.miraculixx.mutils.utils.text.cHighlight
import de.miraculixx.mutils.utils.text.cmp
import de.miraculixx.mutils.utils.text.plus
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

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

            2 -> {
                GUIBuilder(player, GUI.CREATOR_DELETE, GUIAnimation.SPLIT).storage(null, getAllItems()).open()
                player.click()
            }

            3 -> {
                GUIBuilder(player, GUI.CREATOR_LIST, GUIAnimation.SPLIT).scroll(0, getAllItems()).open()
                player.click()
            }
        }
    }

    private fun getAllItems(): Map<ItemStack, Boolean> {
        val tools = CreatorInvTools()
        return tools.getAllItems(cmp("Sneak click", cHighlight) + cmp(" ≫ Delete (FOREVER)"))
    }
}