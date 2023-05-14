package de.miraculixx.mtimer.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.items.ItemsColorBuilder
import de.miraculixx.mtimer.vanilla.data.ColorBuilder
import de.miraculixx.mtimer.vanilla.data.ColorType
import de.miraculixx.mtimer.vanilla.data.GradientBuilder
import de.miraculixx.mtimer.vanilla.data.TimerGUI
import de.miraculixx.mvanilla.extensions.*
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class GUIGradientEditor(data: GradientBuilder) : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event

        when (val id = item.itemMeta?.customModel ?: 0) {
            1 -> {
                data.isAnimated = !data.isAnimated
                if (data.isAnimated) player.soundEnable() else player.soundDisable()
                inv.update()
            }

            2 -> {
                if (data.colors.size < 2) {
                    player.soundError()
                    player.sendMessage(prefix + msg("event.notEnoughColors"))
                    return@event
                }
                val formatted = buildString {
                    append("<gradient")
                    data.colors.forEach { color -> append(":" + color.getColor().asHexString()) }
                    if (data.isAnimated) append(":<x>")
                    append(">")
                }
                player.closeInventory()
                player.sendMessage(
                    prefix + (cmp(formatted, cMark) + cmp(" (click to copy)")).addHover(cmp("Paste/use this color with ctrl + v\n$formatted"))
                        .clickEvent(ClickEvent.copyToClipboard(formatted))
                )
                player.soundEnable()
            }

            else -> {
                val index = id - 10
                if (index in 0..4) {
                    val color = data.colors.getOrNull(index)
                    if (color == null) { //New Color
                        val newColor = ColorBuilder(ColorType.RGB, "white", 0, 0, 0)
                        TimerGUI.COLOR.buildInventory(player, "${player.uniqueId}-COLOR", ItemsColorBuilder(newColor), GUIColorBuilder(newColor, inv))
                        data.colors.add(newColor)
                    } else {
                        TimerGUI.COLOR.buildInventory(player, "${player.uniqueId}-COLOR", ItemsColorBuilder(color), GUIColorBuilder(color, inv))
                    }
                    player.click()

                } else player.soundStone()
            }
        }
    }
}