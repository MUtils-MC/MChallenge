package de.miraculixx.mutils.modules.creator.gui

import de.miraculixx.mutils.Manager
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.enums.settings.gui.StorageFilter
import de.miraculixx.mutils.modules.creator.data.ActionData
import de.miraculixx.mutils.modules.creator.data.CustomChallengeData
import de.miraculixx.mutils.modules.creator.enums.*
import de.miraculixx.mutils.modules.creator.tools.CreatorInvTools
import de.miraculixx.mutils.utils.await.AwaitChatMessage
import de.miraculixx.mutils.utils.await.AwaitItemSelection
import de.miraculixx.mutils.utils.await.AwaitSoundSelections
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.gui.InvUtils
import de.miraculixx.mutils.utils.gui.items.ItemLib
import de.miraculixx.mutils.utils.gui.items.buildItem
import de.miraculixx.mutils.utils.mm
import de.miraculixx.mutils.utils.plainSerializer
import de.miraculixx.mutils.utils.text.*
import de.miraculixx.mutils.utils.tools.click
import de.miraculixx.mutils.utils.tools.soundDelete
import de.miraculixx.mutils.utils.tools.soundEnable
import net.axay.kspigot.items.customModel
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.runnables.sync
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class CreatorActionEditor(val it: InventoryClickEvent) {
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
        val event = tools.getEvent(inventory) ?: return

        when (val id = item.itemMeta?.customModel ?: 0) {
            // NAVIGATION
            200 -> {
                when (inventory.size) {
                    3 * 9 -> if (inventory.getItem(12)?.type == Material.CHEST)
                            GUIBuilder(player, GUI.CREATOR_MODIFY_EVENTS, GUIAnimation.MOVE_RIGHT).scroll(0, tools.getEvents(challenge))
                            .addIndicator(0, "gui.creator.uuid", uuid).open()
                        else openActiveActions(player, challenge, event, GUIAnimation.MOVE_RIGHT)

                    6 * 9 -> GUIBuilder(player, GUI.CREATOR_MODIFY_ACTIONS, GUIAnimation.MOVE_RIGHT).custom(import = ItemLib().getCreator(3, challenge, event)).open()
                }
                player.click()
            }

            // ACTIVE ACTIONS
            in 1000..1999 -> {
                val actionUUID = getActionUUID(inventory, it.slot) ?: return
                if (!it.click.isShiftClick) { // MODIFY DATA
                    openActionValues(player, challenge.eventData[event]?.actions?.get(actionUUID) ?: return, event, uuid, GUIAnimation.MOVE_LEFT)
                } else {
                    challenge.eventData[event]?.actions?.remove(actionUUID)
                    openActiveActions(player, challenge, event)
                    player.soundDelete()
                }
            }

            // LIBRARY ACTION
            in 2000..2999 -> {
                val action = getAction(id) ?: return
                val actionUUID = UUID.randomUUID()
                challenge.eventData[event]?.actions?.put(actionUUID, ActionData(actionUUID, action, action.inputType.map { "None" }.toMutableList()))
                openActiveActions(player, challenge, event, GUIAnimation.MOVE_LEFT)
                player.soundEnable()
            }

            // ACTION SETTINGS
            in 600..610 -> {
                val actionUUID = getActionUUID(inventory, 0) ?: return
                val settingsID = id - 600
                val settings = challenge.eventData[event]?.actions?.get(actionUUID)
                val inputType = settings?.action?.inputType?.get(settingsID)
                if (settings == null || inputType == null) {
                    consoleWarn("Could not read Challenge Data! Error Code: 6 (${settings?.action?.name})")
                    return
                }
                val oldValue = settings.settings[settingsID]
                when (inputType.type) {
                    CreatorActionInput.MATERIAL -> AwaitChatMessage(false, player, "Item Name", 60, {
                        AwaitItemSelection(player, plainSerializer.serialize(it), GUI.CREATOR_MODIFY_ACTIONS) { item ->
                            settings.settings[settingsID] = item.type.name
                            openActionValues(player, settings, event, uuid)
                        }
                    }, {
                        if (player.openInventory.topInventory.type != InventoryType.CHEST) openActionValues(player, settings, event, uuid)
                    })
                    CreatorActionInput.SELECTOR -> {
                        val currentValue = CreatorActionSelector[oldValue] ?: CreatorActionSelector.MOBS_AND_PLAYERS
                        settings.settings[settingsID] = InvUtils.enumRotate(CreatorActionSelector.values(), currentValue).name
                        openActionValues(player, settings, event, uuid)
                    }
                    CreatorActionInput.SOUND -> AwaitChatMessage(false, player, "Sound Name", 60, {
                        AwaitSoundSelections(player, plainSerializer.serialize(it), GUI.CREATOR_MODIFY_ACTIONS) { item ->
                            settings.settings[settingsID] = InvUtils.getIndicator("gui.await.sound", item) ?: "ENTITY_ENDERMAN_TELEPORT"
                            sync { openActionValues(player, settings, event, uuid) }
                        }
                    }, {
                        if (player.openInventory.topInventory.type != InventoryType.CHEST) openActionValues(player, settings, event, uuid)
                    }, true)
                    CreatorActionInput.INT -> AwaitChatMessage(false, player, "Integer (eg 1, 2)", 60, {
                        val number = plainSerializer.serialize(it).toIntOrNull()
                        settings.settings[settingsID] = number?.toString() ?: "0"
                        player.soundEnable()
                        sync { openActionValues(player, settings, event, uuid) }
                    }, {
                        if (player.openInventory.topInventory.type != InventoryType.CHEST) openActionValues(player, settings, event, uuid)
                    })
                    CreatorActionInput.DOUBLE -> AwaitChatMessage(false, player, "Decimal (eg. 1.0)", 60, {
                        val number = plainSerializer.serialize(it).toDoubleOrNull()
                        settings.settings[settingsID] = number?.toString() ?: "0.0"
                        player.soundEnable()
                        sync { openActionValues(player, settings, event, uuid) }
                    }, {
                        if (player.openInventory.topInventory.type != InventoryType.CHEST) openActionValues(player, settings, event, uuid)
                    })
                    CreatorActionInput.TEXT -> {
                        player.sendMessage(msg("module.await.placeholder"))
                        AwaitChatMessage(false, player, "Text", 60, {
                            settings.settings[settingsID] = mm.serialize(it)
                            player.soundEnable()
                            sync { openActionValues(player, settings, event, uuid) }
                        }, {
                            if (player.openInventory.topInventory.type != InventoryType.CHEST) openActionValues(player, settings, event, uuid)
                        }, true)
                    }
                }
                player.click()
            }

            // ACTION SWITCHER
            501 -> { //Saved Actions
                openActiveActions(player, challenge, event, GUIAnimation.MOVE_LEFT)
                player.click()
            }

            502 -> { //Action Lib
                val head = buildItem(Material.BOOKSHELF, 210, cmp("Action Library", cHighlight, bold = true), emptyList())
                GUIBuilder(player, GUI.CREATOR_MODIFY_ACTIONS, GUIAnimation.MOVE_LEFT)
                    .storage(StorageFilter.HIDE, getActions(event), head)
                    .addIndicator(0, "gui.creator.uuid", uuid)
                    .addIndicator(0, "gui.creator.event", event.ordinal.toString()).open()
                player.click()
            }
        }
    }

    //All Actions - Library
    private fun getActions(filter: CreatorEvent): Map<ItemStack, Boolean> {
        return buildMap {
            val l = listOf(emptyComponent(), cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
            val l2 = listOf(emptyComponent(), cmp("Click ", cHighlight) + cmp("≫ Add Action"))
            CreatorAction.values().forEach { action ->
                if (filter.interfaces.contains(action.type) || action.type == EventType.GENERAL) put(
                    buildItem(action.material, action.ordinal + 2000, cmp(action.name.fancy(), cHighlight, bold = true), buildList {
                        addAll(l)
                        addAll(getComponentList("item.CreatorCreate.actions.${action.name}"))
                        addAll(l2)
                    }), false
                )
            }
        }
    }

    //Àll Active Actions
    private fun getActions(challengeData: CustomChallengeData, event: CreatorEvent): Map<ItemStack, Boolean> {
        return buildMap {
            val info = listOf(emptyComponent(), cmp("∙ ") + cmp("Info", cHighlight, underlined = true))
            val settings = listOf(emptyComponent(), cmp("∙ ") + cmp("Settings", cHighlight, underlined = true))
            val click = listOf(emptyComponent(), cmp("Click ", cHighlight) + cmp("≫ Modify Value/s"), cmp("Sneak click ", cHighlight) + cmp("≫ Remove Action"))
            challengeData.eventData[event]?.actions?.forEach { (uuid, actionData) ->
                put(
                    itemStack(actionData.action.material) {
                        meta {
                            customModel = actionData.action.ordinal + 1000
                            displayName(cmp(actionData.action.name.fancy(), cHighlight, bold = true))
                            persistentDataContainer.set(NamespacedKey(Manager, "gui.creator.actionUUID"), PersistentDataType.STRING, uuid.toString())
                            lore(buildList {
                                addAll(info)
                                addAll(getComponentList("item.CreatorCreate.actions.${actionData.action.name}"))
                                addAll(settings)
                                when (actionData.settings.size) {
                                    0 -> add(cmp("   None", italic = true))
                                    1 -> add(cmp("   Value: ") + cmp(actionData.settings.first(), cHighlight))
                                    else -> add(cmp("   Values: ") + cmp(buildString {
                                        actionData.settings.forEach { param ->
                                            append("$param, ")
                                        }
                                    }.removeSuffix(", "), cHighlight))
                                }
                                addAll(click)
                            })
                        }
                    }, false
                )
            }
        }
    }

    private fun getAction(id: Int): CreatorAction? {
        val action = CreatorAction.getByOrdinal(id - 2000)
        if (action == null) {
            consoleWarn("Could not read Challenge Data! Error Code: 3 (${id.minus(2000)})")
        }
        return action
    }

    private fun openActiveActions(player: Player, challenge: CustomChallengeData, event: CreatorEvent, animation: GUIAnimation = GUIAnimation.DEFAULT) {
        val head = buildItem(Material.ENDER_CHEST, 210, cmp("Active Actions", cHighlight, bold = true), emptyList())
        GUIBuilder(player, GUI.CREATOR_MODIFY_ACTIONS, animation).storage(StorageFilter.HIDE, getActions(challenge, event), head)
            .addIndicator(0, "gui.creator.uuid", challenge.uuid.toString())
            .addIndicator(0, "gui.creator.event", event.ordinal.toString()).open()
    }

    private fun getActionUUID(inventory: Inventory, slot: Int): UUID? {
        val indicator = InvUtils.getIndicator("gui.creator.actionUUID", inventory, slot)
        return try {
            UUID.fromString(indicator ?: " ")
        } catch (_: IllegalArgumentException) {
            consoleWarn("Could not read Challenge Data! Error Code: 5 ($indicator)")
            null
        }
    }

    private fun openActionValues(player: Player, actionData: ActionData, event: CreatorEvent, uuid: String, animation: GUIAnimation = GUIAnimation.DEFAULT) {
        val settings = cmp("∙ ") + cmp("Settings", cHighlight, underlined = true)
        val value = cmp("   Value: ")
        val clickInfo = cmp("Click ", cHighlight) + cmp("≫ Change Value")
        val items = buildList {
            actionData.action.inputType.forEachIndexed { index, inputType ->
                add(
                    buildItem(
                        inputType.type.material, 600 + index, cmp(inputType.name, cHighlight, bold = true), listOf(
                            emptyComponent(),
                            settings,
                            value + cmp(actionData.settings.getOrNull(index) ?: "Nothing", cHighlight),
                            emptyComponent(),
                            clickInfo
                        )
                    )
                )
            }
        }
        GUIBuilder(player, GUI.CREATOR_MODIFY_ACTIONS, animation)
            .settings(0, items)
            .addIndicator(0, "gui.creator.uuid", uuid)
            .addIndicator(0, "gui.creator.event", event.ordinal.toString())
            .addIndicator(0, "gui.creator.actionUUID", actionData.uuid.toString())
            .open()
    }
}