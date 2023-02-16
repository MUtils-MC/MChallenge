package de.miraculixx.mutils.utils.gui

import de.miraculixx.api.utils.gui.GUITypes
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.InventoryManager
import de.miraculixx.mutils.gui.items.ItemProvider
import de.miraculixx.mutils.messages.namespace
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player

fun GUITypes.buildInventory(player: Player, id: String, itemProvider: ItemProvider?, clickAction: GUIEvent) {
    InventoryManager.get(id)?.open(player) ?: when (this) {
        GUITypes.CHALLENGE_MENU -> InventoryManager.scrollBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.dataKeys = listOf(NamespacedKey(namespace, "gui.challenge"))
            this.filterable = true
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
        }

        GUITypes.SPEC_PLAYER_OVERVIEW -> InventoryManager.storageBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
        }

        GUITypes.SPEC_SETTINGS -> InventoryManager.inventoryBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.size = 3 * 9
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
        }

        GUITypes.CHALLENGE_SETTINGS -> InventoryManager.settingsBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
        }
    }
}