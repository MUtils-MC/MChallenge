package de.miraculixx.mtimer.gui.actions

import de.miraculixx.mtimer.vanilla.data.ColorBuilder
import de.miraculixx.mtimer.vanilla.data.ColorType
import de.miraculixx.mutils.gui.await.AwaitChatMessage
import de.miraculixx.mutils.gui.data.CustomInventory
import de.miraculixx.mutils.gui.data.GUIClick
import de.miraculixx.mutils.gui.data.GUIEvent
import de.miraculixx.mutils.gui.event.GUIClickEvent
import de.miraculixx.mvanilla.extensions.*
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.minecraft.server.level.ServerPlayer

class GUIColorBuilder(data: ColorBuilder, prevInv: CustomInventory? = null) : GUIEvent {
    override val run: (GUIClickEvent, CustomInventory) -> Unit = event@{ it: GUIClickEvent, inv: CustomInventory ->
        it.isCancelled = true
        val player = it.player as ServerPlayer
        val item = it.item

        when (item.getTagElement(namespace)?.getInt("ID")) {
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

            6 -> AwaitChatMessage(false, player, "Hexcode (#000000)", 60, data.input, false, emptyComponent(), { input ->
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
                    player.closeContainer()
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

    private fun ServerPlayer.calcNumber(old: Int, click: GUIClick): Int {
        return (old + when (click) {
            GUIClick.LEFT_CLICK -> {
                soundUp()
                1
            }

            GUIClick.RIGHT_CLICK -> {
                soundDown()
                -1
            }

            GUIClick.SHIFT_LEFT_CLICK -> {
                soundUp()
                10
            }

            GUIClick.SHIFT_RIGHT_CLICK -> {
                soundDown()
                -10
            }

            else -> 0
        }).coerceIn(0..255)
    }
}