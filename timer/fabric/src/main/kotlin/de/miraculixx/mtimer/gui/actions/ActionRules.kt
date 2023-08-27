package de.miraculixx.mtimer.gui.actions

import de.miraculixx.mtimer.vanilla.data.Punishment
import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.content.ItemsOverview
import de.miraculixx.mtimer.vanilla.data.TimerGUI
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mtimer.vanilla.module.rules
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.GUIEvent
import de.miraculixx.mutils.gui.event.GUIClickEvent
import de.miraculixx.mutils.gui.utils.InventoryUtils.getID
import de.miraculixx.mutils.gui.utils.adv
import de.miraculixx.mvanilla.extensions.click
import de.miraculixx.mvanilla.extensions.enumRotate
import de.miraculixx.mvanilla.extensions.soundUp
import de.miraculixx.mvanilla.extensions.toggle

class ActionRules : GUIEvent {
    override val run: (GUIClickEvent, CustomInventory) -> Unit = event@{ it: GUIClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.player
        val audience = player.adv()
        val item = it.item

        when (item.getID()) {
            1 -> rules.announceSeed = rules.announceSeed.toggle(audience)
            2 -> rules.announceLocation = rules.announceLocation.toggle(audience)
            3 -> rules.specOnDeath = rules.specOnDeath.toggle(audience)
            4 -> rules.specOnJoin = rules.specOnJoin.toggle(audience)
            5 -> {
                if (it.click.isLeftClick()) rules.punishmentSetting.active = rules.punishmentSetting.active.toggle(audience)
                else {
                    rules.punishmentSetting.type = Punishment.values().enumRotate(rules.punishmentSetting.type)
                    audience.soundUp()
                    inv.update()
                }
            }

            6 -> rules.freezeWorld = rules.freezeWorld.toggle(audience)
            7 -> rules.announceBack = rules.announceBack.toggle(audience)
            8 -> rules.syncWithChallenge = rules.syncWithChallenge.toggle(audience)

            0 -> {
                audience.click()
                TimerGUI.OVERVIEW.buildInventory(player, "TIMER_GLOBAL", ItemsOverview(TimerManager.globalTimer, false), ActionOverview(false))
                return@event
            }
        }
        inv.update()
    }
}