package de.miraculixx.mutils.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.extensions.click
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.InventoryUtils.toggle
import de.miraculixx.mutils.gui.TimerGUI
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.items.ItemsOverview
import de.miraculixx.mutils.module.TimerManager
import de.miraculixx.mutils.module.goals
import de.miraculixx.mutils.settings
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class GUIGoals : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event

        when (item.itemMeta?.customModel ?: 0) {
            1 -> goals.enderDragon = . toggle ("Goals.Dragon"
                , inv, player)
                2

            -> settings.toggle("Goals.Wither", inv, player)
            3 -> settings.toggle("Goals.ElderGuardian", inv, player)
            4 -> settings.toggle("Goals.Warden", inv, player)
            5 -> settings.toggle("Goals.Player", inv, player)
            6 -> settings.toggle("Goals.LastPlayer", inv, player)

            0 -> {
                player.click()
                TimerGUI.OVERVIEW.buildInventory(player, "TIMER_GLOBAL", ItemsOverview(TimerManager.getGlobalTimer(), false), GUIOverview(false))
            }
        }
    }
}