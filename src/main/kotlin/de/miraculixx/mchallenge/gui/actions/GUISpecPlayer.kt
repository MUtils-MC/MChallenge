package de.miraculixx.mchallenge.gui.actions

import de.miraculixx.kpaper.extensions.bukkit.msg
import de.miraculixx.kpaper.gui.GUIEvent
import de.miraculixx.kpaper.gui.data.CustomInventory
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix
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
                player.sendMessage(prefix + player.msg("event.playerOffline", listOf(target.name ?: "Unknown")))
                return@event
            }
        }
    }
}