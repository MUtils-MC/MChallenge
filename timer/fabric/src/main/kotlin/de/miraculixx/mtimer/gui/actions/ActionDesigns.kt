package de.miraculixx.mtimer.gui.actions

import de.miraculixx.mtimer.configFolder
import de.miraculixx.mtimer.data.TimerDesign
import de.miraculixx.mtimer.data.TimerPresets
import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.content.ItemsDesignEditor
import de.miraculixx.mtimer.gui.content.ItemsOverview
import de.miraculixx.mtimer.vanilla.data.TimerGUI
import de.miraculixx.mtimer.vanilla.module.Timer
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.GUIClick
import de.miraculixx.mutils.gui.data.GUIEvent
import de.miraculixx.mutils.gui.event.GUIClickEvent
import de.miraculixx.mutils.gui.utils.InventoryUtils.getID
import de.miraculixx.mvanilla.extensions.*
import de.miraculixx.mvanilla.messages.namespace
import net.kyori.adventure.audience.Audience
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import java.io.File
import java.util.*

class ActionDesigns(private val isPersonal: Boolean, private val timer: Timer) : GUIEvent {
    override val run: (GUIClickEvent, CustomInventory) -> Unit = event@{ it: GUIClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.player as? ServerPlayer ?: return@event
        val item = it.item

        when (item.getID()) {
            0 -> {
                player.closeContainer()
                player.click()
                val id = if (isPersonal) player.uuid.toString() else "TIMER_GLOBAL"
                TimerGUI.OVERVIEW.buildInventory(player, id, ItemsOverview(timer, isPersonal), ActionOverview(isPersonal))
            }

            1 -> {
                player.closeContainer()
                player.click()
                val preset = TimerPresets.PRESET
                val uuid = UUID.randomUUID()
                val design = preset.design.copy(name = uuid.toString())
                design.owner = player.scoreboardName
                TimerManager.addDesign(design, uuid)
                TimerGUI.DESIGN_EDITOR.buildInventory(player, "${player.uuid}-EDITOR", ItemsDesignEditor(design, uuid), ActionDesignEditor(design, uuid, isPersonal))
            }

            2 -> { // TODO
//                player.closeInventory()
//                val bridge = MTimer.bridgeAPI
//                if (bridge == null) {
//                    player.soundError()
//                    player.sendMessage(
//                        prefix + cmp("The public library is not implemented yet!\nCheckout ", cError) +
//                                cmp("MUtils.de", cError, underlined = true).clickEvent(ClickEvent.openUrl("https://mutils.de")) +
//                                cmp(" for more information", cError)
//                    )
////                    player.sendMessage(msgNoBridge)
//                } else {
//                    TODO()
//                    //Open GUI
//                }
            }

            10 -> {
                when (it.click) {
                    GUIClick.LEFT_CLICK -> {
                        timer.design = item.getDesign(player)?.first ?: return@event
                        player.soundEnable()
                        inv.update()
                    }

                    GUIClick.RIGHT_CLICK -> {
                        val design = item.getDesign(player) ?: return@event
                        player.click()
                        player.closeContainer()
                        TimerGUI.DESIGN_EDITOR.buildInventory(
                            player, player.uuid.toString(),
                            ItemsDesignEditor(design.first, design.second), ActionDesignEditor(design.first, design.second, isPersonal)
                        )
                    }

                    GUIClick.SHIFT_RIGHT_CLICK -> {
                        if (item.enchantmentTags.isNotEmpty()) {
                            player.soundError()
                            return@event
                        }
                        val design = item.getDesign(player) ?: return@event
                        TimerManager.removeDesign(design.second, File("${configFolder}/designs"))
                        player.soundDelete()
                        inv.update()
                    }

                    else -> player.soundStone()
                }
            }
        }
    }

    private fun ItemStack.getDesign(player: Audience): Pair<TimerDesign, UUID>? {
        val uuidString = getTagElement(namespace)?.getString("timer-design")
        val uuid = try {
            UUID.fromString(uuidString)
        } catch (_: IllegalArgumentException) {
            player.soundError()
            return null
        }
        val design = TimerManager.getDesign(uuid)
        return if (design == null) {
            player.soundError()
            null
        } else design to uuid
    }
}