package de.miraculixx.mtimer.command

import de.miraculixx.mtimer.vanilla.data.ColorBuilder
import de.miraculixx.mtimer.vanilla.data.ColorType
import de.miraculixx.mtimer.vanilla.data.GradientBuilder
import de.miraculixx.mtimer.gui.TimerGUI
import de.miraculixx.mtimer.gui.actions.GUIColorBuilder
import de.miraculixx.mtimer.gui.actions.GUIGradientEditor
import de.miraculixx.mtimer.gui.items.ItemsColorBuilder
import de.miraculixx.mtimer.gui.items.ItemsGradientBuilder
import de.miraculixx.mvanilla.messages.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class HelperCommand : TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String> {
        return buildList {
            when (args?.size) {
                1, 2 -> addAll(listOf("color", "gradient"))
            }
        }.toMutableList()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage(prefix + cmp("command.noPlayer"))
            return false
        }

        when (args?.getOrNull(0)) {
            "color" -> {
                val newColorBuilder = ColorBuilder(ColorType.RGB, "WHITE", 0, 0, 0)
                TimerGUI.COLOR.buildInventory(sender, "${sender.uniqueId}-COLOR", ItemsColorBuilder(newColorBuilder), GUIColorBuilder(newColorBuilder, null))
            }

            "gradient" -> {
                val newGradientBuilder = GradientBuilder(false, mutableListOf())
                cMark
                TimerGUI.COLOR.buildInventory(sender, "${sender.uniqueId}-GRADIENT", ItemsGradientBuilder(newGradientBuilder), GUIGradientEditor(newGradientBuilder))
            }

            else -> sender.sendMessage(prefix + msg("command.noCommand"))
        }
        return true
    }
}