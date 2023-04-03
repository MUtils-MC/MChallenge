package de.miraculixx.mutils.utils.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mvanilla.extensions.click
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.utils.GUITypes
import de.miraculixx.mutils.utils.checkPermission
import de.miraculixx.mutils.utils.items.ItemsBuilderType
import de.miraculixx.mutils.utils.items.ItemsGameRules
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
                if (!player.checkPermission("mutils.event.overview")) return@event
                GUITypes.WORLD_OVERVIEW.buildInventory(player, player.uniqueId.toString(), ItemsWorlds(player.world.uid), GUIWorlds(inv))
                player.click()
            }

            2 -> {
                if (!player.checkPermission("mutils.event.create")) return@event
                GUITypes.WORLD_CREATOR_TYPE.buildInventory(player, "${player.uniqueId}-TYPE", ItemsBuilderType(), GUIBuilderType())
                player.click()
            }

            4 -> {
                GUITypes.WORLD_RULES.buildInventory(player, "${player.uniqueId}-RULES", ItemsGameRules(player.world), GUIGameRules(player.world))
                player.click()
            }

            5 -> {
                GUITypes.WORLD_GLOBAL_RULES.buildInventory(player, "${player.uniqueId}-GLOBAL", ItemsGameRules(null), GUIGameRules(null))
                player.click()
            }
        }
    }
}