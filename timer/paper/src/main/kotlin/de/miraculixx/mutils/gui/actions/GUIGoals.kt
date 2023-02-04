package de.miraculixx.mutils.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.extensions.click
import de.miraculixx.mutils.extensions.toggle
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.TimerGUI
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.items.ItemsOverview
import de.miraculixx.mutils.module.TimerManager
import de.miraculixx.mutils.module.goals
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class GUIGoals : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, _: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event

        when (item.itemMeta?.customModel ?: 0) {
            1 -> goals.enderDragon = goals.enderDragon.toggle(player)
            2 -> goals.wither = goals.wither.toggle(player)
            3 -> goals.elderGuardian = goals.elderGuardian.toggle(player)
            4 -> goals.warden = goals.warden.toggle(player)
            5 -> goals.playerDeath = goals.playerDeath.toggle(player)
            6 -> goals.emptyServer = goals.emptyServer.toggle(player)

            0 -> {
                player.click()
                TimerGUI.OVERVIEW.buildInventory(player, "TIMER_GLOBAL", ItemsOverview(TimerManager.getGlobalTimer(), false), GUIOverview(false))
            }
        }
    }
}