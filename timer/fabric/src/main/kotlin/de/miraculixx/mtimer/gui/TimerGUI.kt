package de.miraculixx.mtimer.gui

import de.miraculixx.mtimer.vanilla.data.TimerGUI
import de.miraculixx.mutils.gui.data.GUIEvent
import de.miraculixx.mutils.gui.data.InventoryManager
import de.miraculixx.mutils.gui.data.ItemProvider
import net.minecraft.world.entity.player.Player

fun TimerGUI.buildInventory(player: Player, id: String, itemProvider: ItemProvider?, clickAction: GUIEvent) {
    InventoryManager.get(id)?.open(player) ?: when (this) {
        TimerGUI.OVERVIEW, TimerGUI.DESIGN_PART_EDITOR -> InventoryManager.inventoryBuilder(id) {
            this.player = player
            this.size = 4
            this.title = this@buildInventory.title
            this.clickAction = clickAction.run
            this.itemProvider = itemProvider
        }

        TimerGUI.DESIGN_EDITOR -> InventoryManager.inventoryBuilder(id) {
            this.player = player
            this.size = 3
            this.title = this@buildInventory.title
            this.clickAction = clickAction.run
            this.itemProvider = itemProvider
        }

        TimerGUI.RULES, TimerGUI.GOALS -> InventoryManager.scrollBuilder(id) {
            this.player = player
            this.title = this@buildInventory.title
            this.clickAction = clickAction.run
            this.itemProvider = itemProvider
        }

        TimerGUI.DESIGN -> InventoryManager.storageBuilder(id) {
            this.player = player
            this.title = this@buildInventory.title
            this.clickAction = clickAction.run
            this.itemProvider = itemProvider
            this.headers = itemProvider?.getExtra() ?: emptyList()
        }

        else -> Unit
    }
}