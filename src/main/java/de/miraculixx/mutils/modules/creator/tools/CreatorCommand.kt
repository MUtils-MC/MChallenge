package de.miraculixx.mutils.modules.creator.tools

import de.miraculixx.mutils.enums.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.modules.creator.CreatorManager
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.text.msg
import de.miraculixx.mutils.utils.tools.soundDisable
import de.miraculixx.mutils.utils.tools.soundEnable
import de.miraculixx.mutils.utils.tools.soundError
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class CreatorCommand : TabExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(msg("command.notPlayer"))
            return false
        }

        when (args.firstOrNull()) {
            "start" -> {
                if (CreatorManager.status == ChallengeStatus.RUNNING) {
                    sender.sendMessage(msg("command.challenge.alreadyOn"))
                    sender.soundError()
                    return false
                }
                CreatorManager.status = ChallengeStatus.RUNNING
                var counter = 0
                CreatorManager.getActive().forEach { challenge ->
                    counter++
                    challenge.start()
                }
                if (counter == 0) sender.sendMessage(msg("command.creator.empty"))
                else {
                    sender.sendMessage(msg("command.creator.start", sender, counter.toString()))
                    sender.soundEnable()
                }
            }

            "stop" -> {
                if (CreatorManager.status == ChallengeStatus.STOPPED) {
                    sender.sendMessage(msg("command.challenge.alreadyOff"))
                    sender.soundError()
                    return false
                }
                CreatorManager.status = ChallengeStatus.STOPPED
                CreatorManager.getActive().forEach { it.stop() }
                sender.sendMessage(msg("command.creator.stop", sender))
                sender.soundDisable()
            }

            "reload" -> {
                if (CreatorManager.status != ChallengeStatus.STOPPED) {
                    sender.sendMessage(msg("command.creator.needStopped"))
                    sender.soundError()
                    return false
                }
                CreatorManager.loadData()
                onlinePlayers.forEach { it.closeInventory() }
                sender.sendMessage(msg("command.creator.reloaded"))
            }

            else -> GUIBuilder(sender, GUI.CREATOR_MAIN, GUIAnimation.SPLIT).custom().open()
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String> {
        return buildList {
            when (args?.size ?: 0) {
                in 0..1 -> addAll(listOf("start", "stop", "reload"))
                else -> {}
            }
        }.toMutableList()
    }
}