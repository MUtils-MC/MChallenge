package de.miraculixx.mutils.utils.await

import de.miraculixx.mutils.utils.tools.click
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.unregister
import net.axay.kspigot.items.customModel
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class AwaitInventoryClick(player: Player, inventory: Inventory, callback: (ItemStack) -> Unit) {
    private val onClick = listen<InventoryClickEvent> {
        if (it.whoClicked != player) return@listen
        if (it.inventory != inventory) return@listen
        it.isCancelled = true
        val item = it.currentItem ?: return@listen
        if (item.itemMeta?.customModel == -1) {
            callback.invoke(item)
            player.click()
            stop()
        } else return@listen
    }

    private val onInvClose = listen<InventoryCloseEvent> {
        if (it.player != player) return@listen
        if (it.inventory != inventory) return@listen
        if (it.reason == InventoryCloseEvent.Reason.PLUGIN || it.reason == InventoryCloseEvent.Reason.OPEN_NEW) return@listen
        taskRunLater(1, true) { player.openInventory(inventory) }
    }

    private fun stop() {
        onClick.unregister()
        onInvClose.unregister()
    }
}