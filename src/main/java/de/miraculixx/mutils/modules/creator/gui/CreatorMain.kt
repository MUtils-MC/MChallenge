package de.miraculixx.mutils.modules.creator.gui

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.modules.creator.CreatorManager
import de.miraculixx.mutils.modules.creator.data.CustomChallengeData
import de.miraculixx.mutils.modules.creator.tools.CreatorInvTools
import de.miraculixx.mutils.modules.gui.GUITools
import de.miraculixx.mutils.utils.text.cHighlight
import de.miraculixx.mutils.utils.text.cmp
import de.miraculixx.mutils.utils.text.plus
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import de.miraculixx.mutils.utils.tools.gui.items.ItemLib
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.*

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

            1 -> {
                val itemLib = ItemLib()
                val uuid = UUID.randomUUID()
                val customChallenge = CustomChallengeData(uuid, Main.INSTANCE.description.version)
                customChallenge.data.author = player.name
                customChallenge.update()
                CreatorManager.addChallenge(customChallenge)
                GUIBuilder(player, GUI.CREATOR_MODIFY, GUIAnimation.SPLIT).custom(import = itemLib.getCreator(1, customChallenge)).open()
                player.click()
            }

            2 -> {
                GUIBuilder(player, GUI.CREATOR_DELETE, GUIAnimation.SPLIT).storage(null, getAllItems(cmp("Sneak click", cHighlight) + cmp(" ≫ Delete (PERMANENT)"))).open()
                player.click()
            }

            3 -> {
                GUIBuilder(player, GUI.CREATOR_LIST, GUIAnimation.SPLIT).scroll(0, getAllItems(cmp("Left click", cHighlight) + cmp(" ≫ Toggle Active"))).open()
                player.click()
            }
        }
    }

    private fun getAllItems(info: Component): Map<ItemStack, Boolean> {
        val tools = CreatorInvTools()
        return tools.getAllItems(info)
    }
}