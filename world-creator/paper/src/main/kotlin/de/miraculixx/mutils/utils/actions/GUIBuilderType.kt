package de.miraculixx.mutils.utils.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.api.data.WorldData
import de.miraculixx.mvanilla.extensions.click
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.items.ItemsBuilder
import de.miraculixx.mutils.utils.items.ItemsMenu
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class GUIBuilderType : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, _: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event

        when (item.itemMeta?.customModel) {
            1 -> {
                val newWorldData = WorldData("MUtils", "New-Set")
                GUITypes.WORLD_CREATOR.buildInventory(player, "${player.uniqueId}-CREATOR", ItemsBuilder(newWorldData, true), GUIBuilder(newWorldData, true))
            }

            2 -> {
                val newWorldData = WorldData("MUtils", "New-World")
                GUITypes.WORLD_CREATOR.buildInventory(player, "${player.uniqueId}-CREATOR", ItemsBuilder(newWorldData, false), GUIBuilder(newWorldData, false))
            }

            else -> GUITypes.WORLD_MENU.buildInventory(player, "WORLD_MENU", ItemsMenu(), GUIMenu())
        }
        player.click()
    }
}