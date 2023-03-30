package de.miraculixx.mutils.commands

import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.bukkit.register
import de.miraculixx.api.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.messages.msg
import de.miraculixx.mutils.messages.plus
import de.miraculixx.mutils.messages.prefix
import de.miraculixx.mutils.modules.ChallengeManager
import de.miraculixx.api.utils.gui.GUITypes
import de.miraculixx.mutils.utils.gui.actions.GUIChallenge
import de.miraculixx.mutils.utils.gui.buildInventory
import de.miraculixx.mutils.utils.gui.items.ItemsChallenge
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class ChallengeCommand : TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String> {
        return buildList {
            when (args?.size ?: 0) {
                0, 1 -> addAll(listOf("stop", "start", "pause", "resume"))
            }
        }.toMutableList()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (args == null || args.isEmpty()) {
            if (sender is Player) GUITypes.CHALLENGE_MENU.buildInventory(sender, sender.uniqueId.toString(), ItemsChallenge(), GUIChallenge())
            else sender.sendMessage(prefix + msg("command.noPlayer"))
            return true
        }

        when (args.getOrNull(0)?.lowercase()) {
            "stop" -> if (ChallengeManager.stopChallenges()) {
                ChallengeManager.status = ChallengeStatus.STOPPED
                broadcast(prefix + msg("command.challenge.stop", listOf(sender.name)))
            } else sender.sendMessage(prefix + msg("command.challenge.alreadyOff"))

            "start" -> if (ChallengeManager.status == ChallengeStatus.RUNNING) {
                sender.sendMessage(prefix + msg("command.challenge.alreadyOn"))
                return false
            } else if (ChallengeManager.startChallenges()) {
                ChallengeManager.status = ChallengeStatus.RUNNING
                broadcast(prefix + msg("command.challenge.start", listOf(sender.name)))
            } else sender.sendMessage(prefix + msg("command.challenge.failed"))

            "pause" -> if (ChallengeManager.pauseChallenges()) {
                ChallengeManager.status = ChallengeStatus.PAUSED
                broadcast(prefix + msg("command.challenge.pause", listOf(sender.name)))
            } else sender.sendMessage(prefix + msg("command.challenge.alreadyOff"))

            "resume" -> if (ChallengeManager.resumeChallenges()) {
                ChallengeManager.status = ChallengeStatus.RUNNING
                broadcast(prefix + msg("command.challenge.continue", listOf(sender.name)))
            } else sender.sendMessage(prefix + msg("command.challenge.alreadyOff"))

            "bridge-install" -> {

            }
        }
        return true
    }

    init {
        register("ch")
        register("challenge")
    }
}