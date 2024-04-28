package de.miraculixx.mchallenge.gui

import de.miraculixx.kpaper.gui.GUIEvent
import de.miraculixx.kpaper.gui.data.InventoryManager
import de.miraculixx.kpaper.gui.items.ItemProvider
import de.miraculixx.mchallenge.utils.config.ConfigManager
import de.miraculixx.mcommons.namespace
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player

fun GUITypes.buildInventory(player: Player, id: String, itemProvider: ItemProvider?, clickAction: GUIEvent) {
    InventoryManager.get(id)?.open(player) ?: when (this) {
        GUITypes.CHALLENGE_MENU -> {
            if (ConfigManager.settings.gui.compact) {
                InventoryManager.storageBuilder(id) {
                    this.title = this@buildInventory.title
                    this.player = player
                    this.filterable = true
                    this.scrollable = true
                    this.itemProvider = itemProvider
                    this.clickAction = clickAction.run
                }
            } else {
                InventoryManager.scrollBuilder(id) {
                    this.title = this@buildInventory.title
                    this.player = player
                    this.dataKeys = listOf(NamespacedKey(namespace, "gui.challenge"))
                    this.filterable = true
                    this.itemProvider = itemProvider
                    this.clickAction = clickAction.run
                }
            }
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

        GUITypes.SETTINGS_CUSTOM_3 -> InventoryManager.inventoryBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.size = 3
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

        GUITypes.COMPETITION, GUITypes.MAIN_MENU -> InventoryManager.inventoryBuilder(id) {
            this.title = this@buildInventory.title
            this.player = player
            this.size = 4
            this.itemProvider = itemProvider
            this.clickAction = clickAction.run
        }
    }
}