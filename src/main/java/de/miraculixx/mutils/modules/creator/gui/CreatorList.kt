package de.miraculixx.mutils.modules.creator.gui

import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.enums.settings.gui.GUIState
import de.miraculixx.mutils.modules.creator.CreatorManager
import de.miraculixx.mutils.modules.creator.tools.CreatorInvTools
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.InvUtils
import de.miraculixx.mutils.utils.gui.items.ItemLib
import de.miraculixx.mutils.utils.text.cHighlight
import de.miraculixx.mutils.utils.text.cmp
import de.miraculixx.mutils.utils.text.consoleWarn
import de.miraculixx.mutils.utils.text.plus
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.soundError
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.Material
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
        val item = it.currentItem ?: return
        val player = it.whoClicked as Player
        val gui = if (it.clickedInventory?.size == 6*9) GUIState.STORAGE else GUIState.SCROLL
        val top = itemStack(Material.BOOK) { meta {
            name = "§9Challenge List"
            customModel = 0
        }}
        when (val id = item.itemMeta?.customModelData ?: 0) {
            200 -> {
                GUIBuilder(player, GUI.CREATOR_MAIN, GUIAnimation.SPLIT).custom().open()
                player.click()
            }

            201, 205 -> {
                val builder = GUIBuilder(player, GUI.CREATOR_LIST)
                if (gui == GUIState.STORAGE) builder.scroll(0, getAllItems())
                else builder.storage(null, getAllItems(), top)
                builder.open()
                player.click()
            }

            202 -> {
                if (it.isShiftClick) InvUtils.navigate(player, -5, GUI.CREATOR_LIST, gui, getAllItems())
                else InvUtils.navigate(player, -1, GUI.CREATOR_LIST, gui, getAllItems())
            }

            203 -> {
                if (it.isShiftClick) InvUtils.navigate(player, 5, GUI.CREATOR_LIST, gui, getAllItems())
                else InvUtils.navigate(player, 1, GUI.CREATOR_LIST, gui, getAllItems())
            }

            else -> {
                if (it.click.isRightClick) {
                    val uuid = UUID.fromString(InvUtils.getIndicator("gui.creator.challenge", item))
                    val challenge = CreatorManager.getChallenge(uuid)
                    if (challenge == null) {
                        consoleWarn("Could not read Custom Challenge data! Error Code: 7 ($uuid)")
                        return
                    }
                    if (challenge.data.author.uuid != player.uniqueId) {
                        player.sendMessage(cmp("modules.creator.notOwner"))
                        player.soundError()
                        return
                    }
                    GUIBuilder(player, GUI.CREATOR_MODIFY, GUIAnimation.MOVE_DOWN).custom(import = ItemLib().getCreator(1, challenge)).open()
                    player.click()
                } else {
                    toggle(id, player)
                    val builder = GUIBuilder(player, GUI.CREATOR_LIST)
                    if (gui == GUIState.STORAGE) builder.storage(null, getAllItems(), top)
                    else builder.scroll(0, getAllItems())
                    builder.open()
                }
            }
        }
    }

    private fun toggle(id: Int, player: Player) {
        val data = CreatorManager.getChallenge(id - 1) ?: return
        val active = CreatorManager.isActive(data.uuid)
        val config = ConfigManager.getConfig(Configs.CUSTOM_CHALLENGE)
        if (active) player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.4f)
        else player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
        config.set(data.uuid.toString(), !active)
        CreatorManager.setActive(data.uuid, !active)
    }

    private fun getAllItems(): Map<ItemStack, Boolean> {
        val tools = CreatorInvTools()
        return tools.getAllChallengeItems(
            cmp("Right click", cHighlight) + cmp(" ≫ Edit Challenge"),
            cmp("Left click", cHighlight) + cmp(" ≫ Toggle Active")
        )
    }
}