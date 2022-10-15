package de.miraculixx.mutils.modules.creator.tools

import de.miraculixx.mutils.modules.creator.CreatorManager
import de.miraculixx.mutils.modules.creator.data.CustomChallengeData
import de.miraculixx.mutils.modules.creator.enums.CreatorEvent
import de.miraculixx.mutils.modules.creator.enums.EventType
import de.miraculixx.mutils.utils.gui.InvUtils
import de.miraculixx.mutils.utils.text.*
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.kyori.adventure.text.Component
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*

class CreatorInvTools {
    fun getAllChallengeItems(vararg infoLore: Component): Map<ItemStack, Boolean> {
        return buildMap {
            var counter = 1
            val lore = buildList {
                add(emptyComponent())
                addAll(infoLore)
            }
            CreatorManager.getAllChallenges().forEach { challenge ->
                put(
                    CreatorManager.modifyItem(challenge.item, counter, lore),
                    CreatorManager.isActive(challenge.uuid)
                )
                counter++
            }
        }
    }

    /**
     * Returns the master Challenge for this Action
     * @param inventory Inventory with indicator at position 0
     */
    fun getChallenge(inventory: Inventory): CustomChallengeData? {
        val rawID = InvUtils.getIndicator("gui.creator.uuid", inventory)
        val uuid = try {
            UUID.fromString(rawID)
        } catch (_: IllegalArgumentException) {
            consoleWarn("Could not read Challenge Data! Error Code: 1 ($rawID)")
            return null
        }
        return CreatorManager.getChallenge(uuid)
    }

    /**
     * Returns the master Event for this Action
     * @param inventory Inventory with indicator at position 1
     */
    fun getEvent(inventory: Inventory): CreatorEvent? {
        val rawID = InvUtils.getIndicator("gui.creator.event", inventory)
        return try {
            CreatorEvent.getByOrdinal(rawID?.toInt() ?: -1)
        } catch (e: Exception) {
            consoleWarn("Could not read Challenge Data! Error Code: 2 ($rawID)")
            return null
        }
    }

    /**
     * Returns a Map of all activated Event Items
     * @param challengeData Challenge Object
     */
    fun getEvents(challengeData: CustomChallengeData): Map<ItemStack, Boolean> {
        return buildMap {
            val l = listOf(
                emptyComponent(),
                cmp("Shift Left Click ", cHighlight) + cmp("≫ Delete Event"),
                cmp("Right Click ", cHighlight) + cmp("≫ Modify Actions"),
                cmp("Left Click ", cHighlight) + cmp("≫ Toggle")
            )
            challengeData.eventData.forEach { (event, data) ->
                put(getEventItem(event, l, 300), data.active)
            }
        }
    }

    /**
     * Returns a Map of Event Items
     * @param list List of events that should not appear
     */
    fun getEvents(list: List<CreatorEvent>, eventType: EventType): Map<ItemStack, Boolean> {
        return buildMap {
            val l = listOf(emptyComponent(), cmp("Click ", cHighlight) + cmp("≫ Add Event"))
            CreatorEvent.values().filter { !list.contains(it) && ((eventType == EventType.NO_FILTER) || it.interfaces.contains(eventType)) }.forEach { event ->
                put(getEventItem(event, l, 400), false)
            }
        }
    }

    private fun getEventItem(event: CreatorEvent, sublore: List<Component>, indicator: Int): ItemStack {
        return itemStack(event.material) {
            meta {
                customModel = event.ordinal + indicator
                displayName(cmp(event.name.fancy(), cHighlight, bold = true))
                lore(
                    buildList {
                        add(emptyComponent())
                        add(cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
                        add(cmp("   " + msg("item.creator.events.indicator", pre = false)))
                        addAll(getComponentList("item.creator.events.${event.name}"))
                        addAll(sublore)
                    }
                )
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            }
        }
    }
}