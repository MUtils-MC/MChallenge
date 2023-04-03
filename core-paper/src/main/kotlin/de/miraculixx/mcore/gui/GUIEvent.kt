package de.miraculixx.mcore.gui

import de.miraculixx.mcore.gui.data.CustomInventory
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

interface GUIEvent {
    val run: (InventoryClickEvent, CustomInventory) -> Unit
    val close: ((InventoryCloseEvent, CustomInventory) -> Unit)?
        get() = null
}