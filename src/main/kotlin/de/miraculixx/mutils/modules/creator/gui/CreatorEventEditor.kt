package de.miraculixx.mutils.modules.creator.gui

import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.enums.settings.gui.StorageFilter
import de.miraculixx.mutils.modules.creator.data.CustomChallengeData
import de.miraculixx.mutils.modules.creator.data.EventData
import de.miraculixx.mutils.modules.creator.enums.CreatorEvent
import de.miraculixx.mutils.modules.creator.enums.EventType
import de.miraculixx.mutils.modules.creator.tools.CreatorInvTools
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.InvUtils
import de.miraculixx.mutils.utils.gui.items.ItemLib
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.soundDelete
import de.miraculixx.mutils.utils.tools.soundDisable
import de.miraculixx.mutils.utils.tools.soundEnable
import net.axay.kspigot.items.customModel
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class CreatorEventEditor(val it: InventoryClickEvent) {
    init {
        event()
    }

    private fun event() {
        val item = it.currentItem ?: return
        val player = it.whoClicked as Player
        val tools = CreatorInvTools()
        val inventory = it.view.topInventory
        val challenge = tools.getChallenge(inventory) ?: return
        val uuid = challenge.uuid.toString()
        val lib = ItemLib()

        when (val id = item.itemMeta?.customModel ?: 0) {
            //Navigation
            200 -> {
                when (inventory.size) {
                    4 * 9, 6 * 9 -> GUIBuilder(player, GUI.CREATOR_MODIFY, GUIAnimation.MOVE_RIGHT)
                        .custom(import = lib.getCreator(2, challenge)).open()

                    3 * 9 -> GUIBuilder(player, GUI.CREATOR_MODIFY_EVENTS, GUIAnimation.MOVE_RIGHT)
                        .scroll(0, tools.getEvents(challenge))
                        .addIndicator(0, "gui.creator.uuid", uuid).open()
                }
                player.click()
            }

            205 -> { //Filter Rotation
                val currentFilter = InvUtils.getCurrentFilter(item)
                val validFilters = arrayOf(StorageFilter.PLAYER_EVENT, StorageFilter.ENTITY_EVENT, StorageFilter.CANCELABLE, StorageFilter.NO_FILTER)
                val newFilter = InvUtils.enumRotate(validFilters, currentFilter)
                GUIBuilder(player, GUI.CREATOR_MODIFY_EVENTS)
                    .storage(newFilter, tools.getEvents(challenge.eventData.keys.toList(), EventType.valueOf(newFilter.name)))
                    .addIndicator(0, "gui.creator.uuid", uuid).open()
                player.click()
            }

            //Active Events
            in 300..399 -> {
                val event = CreatorEvent.getByOrdinal(id - 300) ?: return
                val click = it.click
                if (click.isShiftClick) {
                    challenge.eventData.remove(event)
                    GUIBuilder(player, GUI.CREATOR_MODIFY_EVENTS).scroll(0, tools.getEvents(challenge))
                        .addIndicator(0, "gui.creator.uuid", uuid).open()
                    player.soundDelete()
                } else {
                    if (click == ClickType.LEFT) {
                        toggle(challenge, event, player)
                        GUIBuilder(player, GUI.CREATOR_MODIFY_EVENTS).scroll(0, tools.getEvents(challenge))
                            .addIndicator(0, "gui.creator.uuid", uuid).open()
                    } else if (click == ClickType.RIGHT) {
                        val items = ItemLib().getCreator(3, challenge, event)
                        GUIBuilder(player, GUI.CREATOR_MODIFY_ACTIONS, GUIAnimation.MOVE_LEFT).custom(import = items)
                            .addIndicator(0, "gui.creator.uuid", uuid)
                            .addIndicator(0, "gui.creator.event", event.ordinal.toString()).open()
                    }
                    player.click()
                }
            }

            //Event Library
            in 400..499 -> {
                val event = CreatorEvent.getByOrdinal(id - 400) ?: return
                challenge.eventData[event] = EventData(true, mutableMapOf())
                GUIBuilder(player, GUI.CREATOR_MODIFY_EVENTS).scroll(0, tools.getEvents(challenge))
                    .addIndicator(0, "gui.creator.uuid", uuid).open()
                player.soundEnable()
            }
        }
    }

    private fun toggle(challengeData: CustomChallengeData, event: CreatorEvent, player: Player) {
        challengeData.eventData[event]?.active = if (challengeData.eventData[event]?.active ?: return) {
            player.soundDisable()
            false
        } else {
            player.soundEnable()
            true
        }
    }

    // Liste aus Actions
    // -> Filter nach Event Type (check)
    // -> Filter nach bereits vorhanden
    // -> Action kann mehrmals vorhanden sein
}