package de.miraculixx.mutils.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.kpaper.runnables.async
import de.miraculixx.mutils.MTimer
import de.miraculixx.mutils.await.AwaitChatMessage
import de.miraculixx.mutils.data.TimerDesign
import de.miraculixx.mutils.extensions.click
import de.miraculixx.mutils.extensions.soundEnable
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.TimerGUI
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.items.ItemsDesignPartEditor
import de.miraculixx.mutils.gui.items.ItemsDesigns
import de.miraculixx.mutils.module.TimerManager
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.*

class GUIDesignEditor(
    private val design: TimerDesign,
    private val uuid: UUID,
    private val isPersonal: Boolean,
) : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem
        when (item?.itemMeta?.customModel ?: 0) {
            1 -> AwaitChatMessage(false, player, "design name", 30, design.name, {
                design.name = if (it.length > 30) it.dropLast(it.length - 30) else it
                player.soundEnable()
            }) {
                TimerGUI.DESIGN_EDITOR.buildInventory(player, player.uniqueId.toString(), inv.itemProvider, this)
            }

            2 -> {
                player.closeInventory()
                player.click()
                TimerGUI.DESIGN_PART_EDITOR.buildInventory(player, player.uniqueId.toString(), ItemsDesignPartEditor(design, uuid, true), GUIDesignPartEditor(design, uuid, true, isPersonal))
            }

            3 -> {
                player.closeInventory()
                player.click()
                TimerGUI.DESIGN_PART_EDITOR.buildInventory(player, player.uniqueId.toString(), ItemsDesignPartEditor(design, uuid, false), GUIDesignPartEditor(design, uuid, false, isPersonal))
            }

            4 -> {
                player.closeInventory()
                player.soundEnable()
                val timer = if (isPersonal) TimerManager.getPersonalTimer(player.uniqueId) ?: return@event else TimerManager.getGlobalTimer()
                async { TimerManager.save(MTimer.configFolder) }
                TimerGUI.DESIGN.buildInventory(player, player.uniqueId.toString(), ItemsDesigns(timer), GUIDesigns(isPersonal, timer))
            }
        }
    }
}