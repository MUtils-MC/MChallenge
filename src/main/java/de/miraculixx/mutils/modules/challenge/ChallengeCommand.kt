package de.miraculixx.mutils.modules.challenge

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.modules.challenges
import de.miraculixx.mutils.utils.msg
import de.miraculixx.mutils.utils.tools.gui.GUIBuilder
import net.axay.kspigot.extensions.broadcast
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*

class ChallengeCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(msg("command.noPlayer"))
            return false
        }
        if (ModuleManager.isActive(Modules.SPEEDRUN)) return false
        if (args.isEmpty()) {
            GUIBuilder(sender, GUI.CHALLENGE, animation = GUIAnimation.WATERFALL_OPEN).scroll(0).open()
            return true
        }

        val manager = ChallengeManager()
        if (args.size == 1) {
            when (args[0].lowercase(Locale.getDefault())) {
                "start" -> {
                    //RUNNING -> Error
                    //PAUSED -> Stop and start
                    //STOPPED -> Start
                    if (challenges == ChallengeStatus.RUNNING) {
                        sender.sendMessage(msg("command.challenge.alreadyOn"))
                        return false
                    }

                    if (!ModuleManager.startChallenges()) {
                        sender.sendMessage(msg("command.challenge.failed"))
                        return false
                    }
                    challenges = ChallengeStatus.RUNNING
                    if (ModuleManager.isActive(Modules.TIMER))
                        ModuleManager.setTimerStatus(true)
                    broadcast(msg("command.challenge.start", sender))
                }

                "stop" -> {
                    if (challenges == ChallengeStatus.STOPPED) {
                        sender.sendMessage(msg("command.challenge.alreadyOff"))
                        return false
                    }

                    challenges = ChallengeStatus.STOPPED
                    if (ModuleManager.isActive(Modules.TIMER))
                        ModuleManager.setTimerStatus(false)
                    manager.stopChallenges(ModuleManager.getChallenges())
                    broadcast(msg("command.challenge.stop", sender))
                }

                "pause" -> {
                    if (challenges != ChallengeStatus.RUNNING) {
                        sender.sendMessage(msg("command.challenge.alreadyOff"))
                        return false
                    }

                    challenges = ChallengeStatus.PAUSED
                    if (ModuleManager.isActive(Modules.TIMER))
                        ModuleManager.setTimerStatus(false)
                    manager.unregisterChallenges(ModuleManager.getChallenges())
                    broadcast(msg("command.challenge.pause", sender))
                }

                "resume","continue" -> {
                    if (challenges != ChallengeStatus.PAUSED) {
                        sender.sendMessage(msg("command.challenge.alreadyOff"))
                        return false
                    }

                    challenges = ChallengeStatus.RUNNING
                    if (ModuleManager.isActive(Modules.TIMER))
                        ModuleManager.setTimerStatus(true)
                    manager.registerChallenges(ModuleManager.getChallenges())
                    broadcast(msg("command.challenge.continue", sender))
                }

                else -> sender.sendMessage(msg("command.challenge.help", pre = false))
            }
            return true
        } else {
            sender.sendMessage(msg("command.challenge.help", pre = false))
            return false
        }
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): MutableList<String> {
        return if (p3.size < 2) mutableListOf("start", "stop", "pause", "resume")
        else mutableListOf()
    }
}