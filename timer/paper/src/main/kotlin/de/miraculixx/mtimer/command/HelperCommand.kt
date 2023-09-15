package de.miraculixx.mtimer.command

import de.miraculixx.mtimer.gui.actions.GUIColorBuilder
import de.miraculixx.mtimer.gui.actions.GUIGradientEditor
import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.items.ItemsColorBuilder
import de.miraculixx.mtimer.gui.items.ItemsGradientBuilder
import de.miraculixx.mtimer.vanilla.data.ColorBuilder
import de.miraculixx.mtimer.vanilla.data.ColorType
import de.miraculixx.mtimer.vanilla.data.GradientBuilder
import de.miraculixx.mtimer.vanilla.data.TimerGUI
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor

class HelperCommand {
    private val onCommand = commandTree("colorful") {
        literalArgument("color") {
            playerExecutor { player, _ ->
                val newColorBuilder = ColorBuilder(ColorType.RGB, "WHITE", 0, 0, 0)
                TimerGUI.COLOR.buildInventory(
                    player,
                    "${player.uniqueId}-COLOR",
                    ItemsColorBuilder(newColorBuilder),
                    GUIColorBuilder(newColorBuilder, null)
                )
            }
        }

        literalArgument("gradient") {
            playerExecutor { player, _ ->
                val newGradientBuilder = GradientBuilder(false, mutableListOf())
                TimerGUI.COLOR.buildInventory(
                    player,
                    "${player.uniqueId}-GRADIENT",
                    ItemsGradientBuilder(newGradientBuilder),
                    GUIGradientEditor(newGradientBuilder)
                )
            }
        }
    }
}
