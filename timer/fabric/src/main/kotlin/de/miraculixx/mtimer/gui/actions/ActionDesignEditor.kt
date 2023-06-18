package de.miraculixx.mtimer.gui.actions

import de.miraculixx.mtimer.configFolder
import de.miraculixx.mtimer.data.TimerDesign
import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.content.ItemsDesignPartEditor
import de.miraculixx.mtimer.gui.content.ItemsDesigns
import de.miraculixx.mtimer.vanilla.data.TimerGUI
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mutils.gui.await.AwaitChatMessage
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.GUIEvent
import de.miraculixx.mutils.gui.event.GUIClickEvent
import de.miraculixx.mutils.gui.utils.InventoryUtils.getID
import de.miraculixx.mvanilla.extensions.click
import de.miraculixx.mvanilla.extensions.soundEnable
import de.miraculixx.mvanilla.messages.cmp
import net.minecraft.server.level.ServerPlayer
import net.silkmc.silk.core.task.mcCoroutineTask
import java.io.File
import java.util.*

class ActionDesignEditor(
    private val design: TimerDesign,
    private val uuid: UUID,
    private val isPersonal: Boolean,
) : GUIEvent {
    override val run: (GUIClickEvent, CustomInventory) -> Unit = event@{ it: GUIClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.player as? ServerPlayer ?: return@event
        val item = it.item
        when (item.getID()) {
            1 -> AwaitChatMessage(false, player, "design name", 30, design.name, false, cmp("\n"), {
                design.name = if (it.length > 30) it.dropLast(it.length - 30) else it
                player.soundEnable()
            }) {
                TimerGUI.DESIGN_EDITOR.buildInventory(player, player.uuid.toString(), inv.itemProvider, this)
            }

            2 -> {
                player.closeContainer()
                player.click()
                TimerGUI.DESIGN_PART_EDITOR.buildInventory(player, "${player.uuid}-EDITOR-RUN", ItemsDesignPartEditor(design, uuid, true), ActionDesignPartEditor(design, uuid, true, isPersonal))
            }

            3 -> {
                player.closeContainer()
                player.click()
                TimerGUI.DESIGN_PART_EDITOR.buildInventory(player, "${player.uuid}-EDITOR-PAUSE", ItemsDesignPartEditor(design, uuid, false), ActionDesignPartEditor(design, uuid, false, isPersonal))
            }

            4 -> {
                player.closeContainer()
                player.soundEnable()
                val timer = if (isPersonal) TimerManager.getPersonalTimer(player.uuid) ?: return@event else TimerManager.globalTimer
                mcCoroutineTask(false) { TimerManager.save(File(configFolder)) }
                TimerGUI.DESIGN.buildInventory(player, "${player.uuid}-DESIGNS", ItemsDesigns(timer), ActionDesigns(isPersonal, timer))
            }
        }
    }
}