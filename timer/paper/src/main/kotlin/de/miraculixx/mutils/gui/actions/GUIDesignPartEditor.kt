package de.miraculixx.mutils.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mutils.await.AwaitChatMessage
import de.miraculixx.mutils.data.TimerDesign
import de.miraculixx.mutils.data.TimerDesignValue
import de.miraculixx.mutils.extensions.*
import de.miraculixx.mutils.gui.GUIEvent
import de.miraculixx.mutils.gui.TimerGUI
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.items.ItemsDesignEditor
import de.miraculixx.mutils.messages.msg
import de.miraculixx.mutils.messages.plus
import de.miraculixx.mutils.messages.prefix
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.*

class GUIDesignPartEditor(
    private val design: TimerDesign,
    private val uuid: UUID,
    private val isRunning: Boolean,
    private val isPersonal: Boolean
) : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem
        val part = if (isRunning) design.running else design.idle
        when (item?.itemMeta?.customModel ?: 0) {
            10 -> {
                player.closeInventory()
                player.soundEnable()
                TimerGUI.DESIGN_EDITOR.buildInventory(player, player.uniqueId.toString(), ItemsDesignEditor(design, uuid), GUIDesignEditor(design, uuid, isPersonal))
            }

            1 -> AwaitChatMessage(false, player, "prefix", 120, part.prefix, {
                part.prefix = if (it.length > 300) it.dropLast(it.length - 300) else it
                player.soundEnable()
            }) { openThis(player, inv) }

            2 -> player.setupValue(part.days, it.click, inv)
            3 -> player.setupValue(part.hours, it.click, inv)
            4 -> player.setupValue(part.minutes, it.click, inv)
            5 -> player.setupValue(part.seconds, it.click, inv)
            6 -> player.setupValue(part.millis, it.click, inv)

            7 -> AwaitChatMessage(false, player, "suffix", 120, part.suffix, {
                part.suffix = if (it.length > 300) it.dropLast(it.length - 300) else it
                player.soundEnable()
            }) { openThis(player, inv) }

            8 -> {
                when (it.click) {
                    ClickType.LEFT -> if (part.animationSpeed >= 1.0) player.soundError() else {
                        part.animationSpeed += .01f
                        player.soundUp()
                    }

                    ClickType.RIGHT -> if (part.animationSpeed <= -1.0) player.soundError() else {
                        part.animationSpeed -= .01f
                        player.soundDown()
                    }

                    else -> {
                        player.soundStone()
                        return@event
                    }
                }
                inv.update()
            }

            9 -> {
                player.sendMessage(prefix + msg("event.syntaxInfo", listOf(player.name)))
                AwaitChatMessage(false, player, "syntax", 120, part.syntax, {
                    part.syntax = if (it.length > 300) it.dropLast(it.length - 300) else it
                    player.soundEnable()
                }) { openThis(player, inv) }
            }
        }
    }

    private fun Player.setupValue(value: TimerDesignValue, click: ClickType, inv: CustomInventory) {
        when (click) {
            ClickType.LEFT -> {
                if (value.forcedTwoDigits) soundDisable() else soundEnable()
                value.forcedTwoDigits = !value.forcedTwoDigits
            }

            ClickType.RIGHT -> {
                if (value.visibleOnNull) soundDisable() else soundEnable()
                value.visibleOnNull = !value.visibleOnNull
            }

            ClickType.SHIFT_LEFT -> AwaitChatMessage(false, this, "prefix", 120, value.prefix, {
                value.prefix = if (it.length > 300) it.dropLast(it.length - 300) else it
                soundEnable()
            }) { openThis(this, inv) }

            ClickType.SHIFT_RIGHT -> AwaitChatMessage(false, this, "suffix", 120, value.suffix, {
                value.suffix = if (it.length > 300) it.dropLast(it.length - 300) else it
                soundEnable()
            }) { openThis(this, inv) }

            else -> {
                soundStone()
                return
            }
        }
        inv.update()
    }

    private fun openThis(player: Player, inventory: CustomInventory) {
        TimerGUI.DESIGN_PART_EDITOR.buildInventory(player, player.uniqueId.toString(), inventory.itemProvider, this)
    }
}