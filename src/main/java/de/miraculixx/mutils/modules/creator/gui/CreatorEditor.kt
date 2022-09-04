package de.miraculixx.mutils.modules.creator.gui

import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.modules.creator.data.CustomChallengeData
import de.miraculixx.mutils.modules.creator.enums.EventType
import de.miraculixx.mutils.modules.creator.tools.CreatorInvTools
import de.miraculixx.mutils.utils.await.AwaitChatMessage
import de.miraculixx.mutils.utils.await.AwaitItemSelection
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.items.ItemLib
import de.miraculixx.mutils.utils.plainSerializer
import de.miraculixx.mutils.utils.text.cHighlight
import de.miraculixx.mutils.utils.text.cmp
import de.miraculixx.mutils.utils.text.plus
import de.miraculixx.mutils.utils.tools.click
import net.axay.kspigot.items.customModel
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType

class CreatorEditor(val it: InventoryClickEvent) {
    init {
        event()
    }

    private fun event() {
        val player = it.whoClicked as Player
        val item = it.currentItem
        val tools = CreatorInvTools()
        val inventory = it.view.topInventory
        val challenge = tools.getChallenge(inventory) ?: return
        val uuid = challenge.uuid.toString()
        val itemLib = ItemLib()

        when (item?.itemMeta?.customModel ?: 0) {
            //Navigation
            200 -> {
                if (inventory.size == 6 * 9) return
                if (inventory.getItem(10)?.itemMeta?.customModel == 200) GUIBuilder(player, GUI.CREATOR_MODIFY, GUIAnimation.MOVE_RIGHT).custom(import = itemLib.getCreator(1, challenge)).open()
                else {
                    challenge.update()
                    GUIBuilder(player, GUI.CREATOR_LIST, GUIAnimation.MOVE_RIGHT).scroll(0, tools.getAllChallengeItems(cmp("Left click", cHighlight) + cmp(" ≫ Toggle Active"))).open()
                }
                player.click()
            }

            //Main Menu
            1 -> {
                awaitChat(player, challenge, "Challenge Name", itemLib)
            }

            2 -> {
                awaitChat(player, challenge, "Challenge Description", itemLib)
            }

            3 -> { // Icon Chooser
                player.closeInventory()
                AwaitChatMessage(true, player, "Item Name", 30, {
                    val message = plainSerializer.serialize(it).uppercase().replace(" ", "_")
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
                    AwaitItemSelection(player, message, GUI.CREATOR_MODIFY) { item ->
                        challenge.data.icon = item.type.name
                        challenge.update()
                        GUIBuilder(player, GUI.CREATOR_MODIFY).custom(import = itemLib.getCreator(1, challenge)).open()
                    }
                }, {
                    if (player.openInventory.topInventory.type == InventoryType.CHEST) return@AwaitChatMessage
                    GUIBuilder(player, GUI.CREATOR_MODIFY).custom(import = itemLib.getCreator(1, challenge)).open()
                })
            }

            4 -> {
                GUIBuilder(player, GUI.CREATOR_MODIFY, GUIAnimation.MOVE_LEFT).custom(import = itemLib.getCreator(2, challenge, null)).open()
                player.click()
            }

            //Event Selection
            101 -> {
                GUIBuilder(player, GUI.CREATOR_MODIFY_EVENTS, GUIAnimation.MOVE_LEFT).scroll(0, tools.getEvents(challenge))
                    .addIndicator(0, "gui.creator.uuid", uuid).open()
                player.click()
            }

            102 -> {
                GUIBuilder(player, GUI.CREATOR_MODIFY_EVENTS, GUIAnimation.MOVE_LEFT).storage(null, tools.getEvents(challenge.eventData.keys.toList(), EventType.NO_FILTER))
                    .addIndicator(0, "gui.creator.uuid", uuid).open()
                player.click()
            }
        }
    }

    private fun awaitChat(player: Player, challengeData: CustomChallengeData, name: String, lib: ItemLib) {
        player.closeInventory()
        AwaitChatMessage(false, player, name, 60, {
            val message = plainSerializer.serialize(it)
            if (name == "Challenge Name") challengeData.data.name = message
            else challengeData.data.description = message
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
        }, {
            challengeData.update()
            GUIBuilder(player, GUI.CREATOR_MODIFY).custom(import = lib.getCreator(1, challengeData)).open()
        })
    }
}