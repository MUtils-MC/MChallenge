package de.miraculixx.mtimer.gui.actions

import de.miraculixx.kpaper.items.customModel
import de.miraculixx.mcore.gui.GUIEvent
import de.miraculixx.mcore.gui.data.CustomInventory
import de.miraculixx.mtimer.data.ColorBuilder
import de.miraculixx.mtimer.data.ColorType
import de.miraculixx.mvanilla.extensions.*
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class GUIColorBuilder(data: ColorBuilder, prevInv: CustomInventory? = null) : GUIEvent {
    override val run: (InventoryClickEvent, CustomInventory) -> Unit = event@{ it: InventoryClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.whoClicked as? Player ?: return@event
        val item = it.currentItem ?: return@event

        when (item.itemMeta?.customModel) {
            1 -> {
                data.type = ColorType.values().enumRotate(data.type)
                player.click()
            }

            2 -> {
                val allNames = NamedTextColor.NAMES
                val current = allNames.valueOr(data.input, NamedTextColor.WHITE)
                data.input = allNames.values().toTypedArray().enumRotate(current).toString()
                player.click()
            }

            3 -> data.r = player.calcNumber(data.r, it.click)
            4 -> data.g = player.calcNumber(data.g, it.click)
            5 -> data.b = player.calcNumber(data.b, it.click)

            6 -> de.miraculixx.mcore.await.AwaitChatMessage(false, player, "Hexcode (#000000)", 60, data.input, false, emptyComponent(), { input ->
                val newColor = TextColor.fromHexString(input)
                if (newColor == null) player.soundError()
                else data.input = input
            }) {
                inv.update()
                inv.open(player)
            }

            10 -> {
                if (prevInv == null) {
                    val formatted = "<color:${data.getColor().asHexString()}>"
                    player.closeInventory()
                    player.sendMessage(
                        prefix + (cmp(formatted, cMark) + cmp(" (click to copy)")).addHover(cmp("Paste/use this color with ctrl + v\n$formatted"))
                            .clickEvent(ClickEvent.copyToClipboard(formatted))
                    )
                    player.soundEnable()
                } else {
                    prevInv.update()
                    prevInv.open(player)
                    player.soundEnable()
                }
            }

            else -> {
                if (prevInv != null) {
                    prevInv.update()
                    prevInv.open(player)
                    player.click()
                } else player.soundStone()
                return@event
            }
        }
        inv.update()
    }

    private fun Player.calcNumber(old: Int, click: ClickType): Int {
        return (old + when (click) {
            ClickType.LEFT -> {
                soundUp()
                1
            }

            ClickType.RIGHT -> {
                soundDown()
                -1
            }

            ClickType.SHIFT_LEFT -> {
                soundUp()
                10
            }

            ClickType.SHIFT_RIGHT -> {
                soundDown()
                -10
            }

            else -> 0
        }).coerceIn(0..255)
    }
}