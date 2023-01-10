package de.miraculixx.mutils.utils.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.data.WorldData
import de.miraculixx.mutils.extensions.click
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.items.ItemsBuilder
import de.miraculixx.mutils.utils.items.ItemsWorlds
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class GUIMenu : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event

        when (item.itemMeta?.customModel) {
            1 -> {
                GUITypes.WORLD_OVERVIEW.buildInventory(player, player.uniqueId.toString(), ItemsWorlds(player.world.uid), GUIWorlds())
                player.click()
            }

            2 -> {
                val newWorldData = WorldData()
                GUITypes.WORLD_CREATOR.buildInventory(player, player.uniqueId.toString(), ItemsBuilder(newWorldData), GUIBuilder(newWorldData))
                player.click()
            }
        }
    }
}