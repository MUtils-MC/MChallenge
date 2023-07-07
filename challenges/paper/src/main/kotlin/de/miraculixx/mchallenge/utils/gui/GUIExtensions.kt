package de.miraculixx.mchallenge.utils.gui

import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.InventoryManager
import de.miraculixx.mcore.gui.items.ItemProvider
import de.miraculixx.mvanilla.messages.namespace
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

        GUITypes.SPEC_PLAYER_OVERVIEW, GUITypes.CH_LOW_VISION -> InventoryManager.storageBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
            clickAction.close?.let { this.closeAction = it }
            this.scrollable = true
            itemProvider?.getExtra()?.let { this.headers = it }
        }

        GUITypes.SPEC_SETTINGS -> InventoryManager.inventoryBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.size = 3 * 9
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
        }

        GUITypes.CHALLENGE_SETTINGS, GUITypes.CH_LIMITED_SKILLS -> InventoryManager.settingsBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
            this.closeAction = clickAction.close
        }

        GUITypes.COMPETITION -> InventoryManager.inventoryBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.size = 4 * 9
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
        }
    }
}