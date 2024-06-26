package de.miraculixx.mchallenge.gui.actions

import de.miraculixx.kpaper.gui.GUIEvent
import de.miraculixx.kpaper.gui.data.CustomInventory
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class GUICompetitionCreator : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event
        val meta = item.itemMeta ?: return@event


    }
}