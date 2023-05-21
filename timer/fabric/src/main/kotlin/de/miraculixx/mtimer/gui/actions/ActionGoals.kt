package de.miraculixx.mtimer.gui.actions

import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.content.ItemsOverview
import de.miraculixx.mtimer.server
import de.miraculixx.mtimer.vanilla.data.TimerGUI
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mtimer.vanilla.module.goals
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.GUIEvent
import de.miraculixx.mutils.gui.event.GUIClickEvent
import de.miraculixx.mutils.gui.utils.InventoryUtils.getID
import de.miraculixx.mutils.gui.utils.adv
import de.miraculixx.mvanilla.extensions.click
import de.miraculixx.mvanilla.extensions.toggle

class ActionGoals : GUIEvent {
    override val run: (GUIClickEvent, CustomInventory) -> Unit = event@{ it: GUIClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.player
        val audience = player.adv()
        val item = it.item

        when (item.getID()) {
            1 -> goals.enderDragon = goals.enderDragon.toggle(audience)
            2 -> goals.wither = goals.wither.toggle(audience)
            3 -> goals.elderGuardian = goals.elderGuardian.toggle(audience)
            4 -> goals.warden = goals.warden.toggle(audience)
            5 -> goals.playerDeath = goals.playerDeath.toggle(audience)
            6 -> goals.emptyServer = goals.emptyServer.toggle(audience)

            0 -> {
                audience.click()
                TimerGUI.OVERVIEW.buildInventory(player, "TIMER_GLOBAL", ItemsOverview(TimerManager.globalTimer, false), ActionOverview(false))
                return@event
            }
        }
        inv.update()
    }
}