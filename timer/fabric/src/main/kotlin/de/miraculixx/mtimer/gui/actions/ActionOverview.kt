package de.miraculixx.mtimer.gui.actions

import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.GUIClick
import de.miraculixx.mutils.gui.data.GUIEvent
import de.miraculixx.mutils.gui.event.GUIClickEvent
import de.miraculixx.mvanilla.extensions.*
import de.miraculixx.mvanilla.messages.*
import net.minecraft.server.level.ServerPlayer

class ActionOverview(isPersonal: Boolean) : GUIEvent {
    private val noPersonalTimer = prefix + msg("event.noPersonalTimer")

    override val run: (GUIClickEvent, CustomInventory) -> Unit = event@{ it: GUIClickEvent, inv: CustomInventory ->
        it.isCancelled = true

        it.isCancelled = true
        val player = it.player as ServerPlayer
        val item = it.item
        val timer = if (isPersonal) TimerManager.getPersonalTimer(player.uuid) else TimerManager.globalTimer
        if (timer == null) {
//            server.playerList.getPlayer(player.uuid)
            player.sendMessage(noPersonalTimer)
            return@event
        }
        when (val id = item.getTagElement(namespace)?.getInt("ID")) {
            5 -> {
                player.closeContainer()
                val guiID = if (isPersonal) player.uuid.toString() else "TIMER_GLOBAL_DESIGNS"
                player.click()
//                TimerGUI.DESIGN.buildInventory(player, guiID, ItemsDesigns(timer), GUIDesigns(isPersonal, timer))
            }

            6 -> if (timer.countUp) {
                timer.countUp = false
                player.soundDisable()
            } else {
                timer.countUp = true
                player.soundEnable()
            }

            7 -> if (timer.visible) {
                timer.visible = false
                player.soundDisable()
                player.sendActionBar(emptyComponent())
            } else {
                timer.visible = true
                player.soundEnable()
            }

            8 -> {
                player.closeContainer()
                player.click()
//                TimerGUI.RULES.buildInventory(player, player.uuid.toString(), ItemsRules(), GUIRules())
                return@event
            }

            9 -> {
                player.closeContainer()
                player.click()
//                TimerGUI.GOALS.buildInventory(player, player.uuid.toString(), ItemsGoals(), GUIGoals())
                return@event
            }

            1, 2, 3, 4 -> {
                // Time Settings
                val timeAdded = when (it.click) {
                    GUIClick.LEFT_CLICK -> {
                        when (id) {
                            1 -> timer.addTime(sec = 1)
                            2 -> timer.addTime(min = 1)
                            3 -> timer.addTime(hour = 1)
                            4 -> timer.addTime(day = 1)
                            else -> false
                        }
                    }

                    GUIClick.RIGHT_CLICK -> {
                        when (id) {
                            1 -> timer.addTime(sec = -1)
                            2 -> timer.addTime(min = -1)
                            3 -> timer.addTime(hour = -1)
                            4 -> timer.addTime(day = -1)
                            else -> false
                        }
                    }

                    GUIClick.SHIFT_LEFT_CLICK -> {
                        when (id) {
                            1 -> timer.addTime(sec = 10)
                            2 -> timer.addTime(min = 10)
                            3 -> timer.addTime(hour = 10)
                            4 -> timer.addTime(day = 10)
                            else -> false
                        }

                    }

                    GUIClick.SHIFT_RIGHT_CLICK -> {
                        when (id) {
                            1 -> timer.addTime(sec = -10)
                            2 -> timer.addTime(min = -10)
                            3 -> timer.addTime(hour = -10)
                            4 -> timer.addTime(day = -10)
                            else -> false
                        }
                    }

                    else -> false
                }

                if (timeAdded) player.soundUp()
                else player.soundStone()
            }

            else -> return@event
        }

        inv.update()
    }
}