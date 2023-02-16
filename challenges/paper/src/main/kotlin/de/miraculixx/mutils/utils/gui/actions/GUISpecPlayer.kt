package de.miraculixx.mutils.utils.gui.actions

import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.messages.msg
import de.miraculixx.mutils.messages.plus
import de.miraculixx.mutils.messages.prefix
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.SkullMeta

class GUISpecPlayer : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event
        val meta = item.itemMeta

        if (meta is SkullMeta) {
            val target = meta.owningPlayer ?: return@event
            if (!target.isOnline) {
                player.sendMessage(prefix + msg("event.playerOffline", listOf(target.name ?: "Unknown")))
                return@event
            }
        }
    }
}