package de.miraculixx.mtimer.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mcore.await.AwaitChatMessage
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mtimer.data.TimerDesign
import de.miraculixx.mtimer.data.TimerDesignValue
import de.miraculixx.mtimer.gui.TimerGUI
import de.miraculixx.mtimer.gui.items.ItemsDesignEditor
import de.miraculixx.mvanilla.extensions.*
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.event.ClickEvent
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
    private val awaitInfoMessage = cmp("Input Formatting", cHighlight, true) +
            cmp("\n ├> ") + (cmp("Simple Color ", cMark) + cmp("(click)")).addHover(cmp("Start - /colorful color")).clickEvent(ClickEvent.runCommand("/colorful color")) +
            cmp("\n ├> ") + (cmp("Gradient ", cMark) + cmp("(click)")).addHover(cmp("Start - /colorful gradient")).clickEvent(ClickEvent.runCommand("/colorful gradient")) +
            cmp("\n ├> ") + cmp("Use ") + cmp("_", cMark) + cmp(" for spaces") +
            cmp("\n ├> ") + cmp("#exit", cMark) + cmp(" to leave - ") + cmp("#clear", cMark) + cmp(" to clear")
    private val maxSeconds = 240

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

            1 -> AwaitChatMessage(false, player, "prefix", maxSeconds, part.prefix,
                true, awaitInfoMessage, {
                    part.prefix = if (it.length > 300) it.dropLast(it.length - 300) else it
                    player.soundEnable()
                }) { openThis(player, inv) }

            2 -> player.setupValue(part.days, inv, it.hotbarButton)
            3 -> player.setupValue(part.hours, inv, it.hotbarButton)
            4 -> player.setupValue(part.minutes, inv, it.hotbarButton)
            5 -> player.setupValue(part.seconds, inv, it.hotbarButton)
            6 -> player.setupValue(part.millis, inv, it.hotbarButton)

            7 -> AwaitChatMessage(false, player, "suffix", maxSeconds, part.suffix,
                true, awaitInfoMessage, {
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
                AwaitChatMessage(false, player, "syntax", maxSeconds, part.syntax,
                    true, awaitInfoMessage, {
                        part.syntax = if (it.length > 300) it.dropLast(it.length - 300) else it
                        player.soundEnable()
                    }) { openThis(player, inv) }
            }
        }
    }

    private fun Player.setupValue(value: TimerDesignValue, inv: CustomInventory, target: Int) {
        when (target) {
            0 -> {
                if (value.forcedTwoDigits) soundDisable() else soundEnable()
                value.forcedTwoDigits = !value.forcedTwoDigits
            }

            1 -> {
                if (value.visibleOnNull) soundDisable() else soundEnable()
                value.visibleOnNull = !value.visibleOnNull
            }

            2 -> {
                AwaitChatMessage(false, this, "prefix", maxSeconds, value.prefix,
                    true, awaitInfoMessage, {
                        value.prefix = if (it.length > 300) it.dropLast(it.length - 300) else it
                        soundEnable()
                    }) { openThis(this, inv) }
            }

            3 -> {
                AwaitChatMessage(false, this, "suffix", maxSeconds, value.suffix,
                    true, awaitInfoMessage, {
                        value.suffix = if (it.length > 300) it.dropLast(it.length - 300) else it
                        soundEnable()
                    }) { openThis(this, inv) }
            }

            else -> soundStone()
        }
        inv.update()
    }

    private fun openThis(player: Player, inventory: CustomInventory) {
        TimerGUI.DESIGN_PART_EDITOR.buildInventory(player, player.uniqueId.toString(), inventory.itemProvider, this)
    }
}