package de.miraculixx.mutils.modules.creator.gui

import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.enums.settings.gui.GUIState
import de.miraculixx.mutils.modules.creator.CreatorManager
import de.miraculixx.mutils.modules.gui.GUITools
import de.miraculixx.mutils.utils.mm
import de.miraculixx.mutils.utils.text.cHighlight
import de.miraculixx.mutils.utils.text.cmp
import de.miraculixx.mutils.utils.text.emptyComponent
import de.miraculixx.mutils.utils.text.plus
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class CreatorList(val it: InventoryClickEvent) {

    init {
        event()
    }

    private fun event() {
        val item = it.currentItem
        val player = it.whoClicked as Player
        val gui = if (it.clickedInventory?.size == 6) GUIState.STORAGE else GUIState.SCROLL
        when (item?.itemMeta?.customModelData) {
            200 -> {
                GUIBuilder(player, GUI.CREATOR_MAIN, GUIAnimation.SPLIT).custom().open()
                player.click()
            }

            201 -> {
                val builder = GUIBuilder(player, GUI.CREATOR_LIST)
                if (it.clickedInventory?.size == 6)
                    builder.scroll(0)
                else builder.storage(null)
                builder.open()
            }

            202 -> {
                val tools = GUITools(null)
                if (it.isShiftClick) tools.navigate(player, 5, GUI.CREATOR_LIST, gui, getAllItems())
                else tools.navigate(player, 1, GUI.CREATOR_LIST, gui, getAllItems())
            }

            203 -> {
                val tools = GUITools(null)
                if (it.isShiftClick) tools.navigate(player, -5, GUI.CREATOR_LIST, gui, getAllItems())
                else tools.navigate(player, -1, GUI.CREATOR_LIST, gui, getAllItems())
            }

            else -> {
                val lore = item?.lore()?.firstOrNull() ?: return
                val raw = mm.stripTags(mm.serialize(lore)) //ID: <uuid>
                val uuid = try {
                    UUID.fromString(raw.removePrefix("ID: "))
                } catch (_: IllegalArgumentException) {
                    return
                }
                toggle(uuid, player)
            }
        }
    }

    private fun toggle(uuid: UUID, player: Player) {
        val active = CreatorManager.isActive(uuid)
        if (active) player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.4f)
        else player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
        CreatorManager.setActive(uuid, !active)
    }

    private fun getAllItems(): Map<ItemStack, Boolean> {
        return buildMap {
            var counter = 1
            CreatorManager.getAllChallenges().forEach { challenge ->
                put(CreatorManager.modifyItem(challenge.item, counter, listOf(emptyComponent(), cmp("Left click", cHighlight) + cmp(" ≫ Toggle Active"))),
                CreatorManager.isActive(challenge.uuid))
            }
            counter++
        }
    }
}