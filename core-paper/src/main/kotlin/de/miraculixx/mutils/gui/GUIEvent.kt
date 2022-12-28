package de.miraculixx.mutils.gui

import de.miraculixx.mutils.gui.data.CustomInventory
import org.bukkit.event.inventory.InventoryClickEvent

interface GUIEvent {
    val run: (InventoryClickEvent, CustomInventory) -> Unit
}