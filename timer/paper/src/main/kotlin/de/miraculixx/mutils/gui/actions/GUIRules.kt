package de.miraculixx.mutils.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.data.Punishment
import de.miraculixx.mutils.extensions.click
import de.miraculixx.mutils.extensions.enumOf
import de.miraculixx.mutils.extensions.enumRotate
import de.miraculixx.mutils.extensions.soundUp
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.InventoryUtils.toggle
import de.miraculixx.mutils.gui.TimerGUI
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.items.ItemsOverview
import de.miraculixx.mutils.module.TimerManager
import de.miraculixx.mutils.settings
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class GUIRules: GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event

        when (item.itemMeta?.customModel ?: 0) {
            1 -> settings.toggle("Rules.announceSeed", inv, player)
            2 -> settings.toggle("Rules.announceLocation", inv, player)
            3 -> settings.toggle("Rules.specOnDeath", inv, player)
            4 -> settings.toggle("Rules.specOnJoin", inv, player)
            5 -> {
                if (it.click.isLeftClick) settings.toggle("Rules.punishment", inv, player)
                else {
                    settings.set(
                        "Rules.punishmentType", Punishment.values().enumRotate(
                            enumOf<Punishment>(settings.getString("Rules.punishmentType") ?: "") ?: Punishment.BAN
                        )
                    )
                    player.soundUp()
                    inv.update()
                }
            }

            0 -> {
                player.click()
                TimerGUI.OVERVIEW.buildInventory(player, "TIMER_GLOBAL", ItemsOverview(TimerManager.getGlobalTimer(), false), GUIOverview(false))
            }
        }
    }
}