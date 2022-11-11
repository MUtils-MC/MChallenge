package de.miraculixx.mutils.modules.timer

import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.enums.modules.challenges.ChallengeStatus
import de.miraculixx.mutils.enums.settings.gui.GUI
import de.miraculixx.mutils.enums.settings.gui.GUIAnimation
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.modules.challenge.ChallengeManager
import de.miraculixx.mutils.modules.challenges
import de.miraculixx.mutils.utils.gui.GUIBuilder
import de.miraculixx.mutils.utils.text.msg
import net.axay.kspigot.extensions.broadcast
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class TimerCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            if (sender is Player)
                GUIBuilder(sender, GUI.TIMER_SETTINGS, GUIAnimation.WATERFALL_OPEN).custom().open()
            else sender.sendMessage(msg("command.timer.help", pre = false))
            return true
        } else if (args.size > 1) {
            sender.sendMessage(msg("command.timer.help", pre = false))
            return false
        }

        if (!ModuleManager.isActive(Modules.TIMER)) {
            sender.sendMessage(msg("module.deactivated", input = "Timer"))
            return false
        }

        val status = ModuleManager.getTimerStatus()
        val chManager = ChallengeManager()
        when (args[0].lowercase()) {
            "resume" -> {
                if (status) {
                    sender.sendMessage(msg("command.timer.alreadyOn"))
                    return false
                }
                ModuleManager.setTimerStatus(true)
                when (challenges) {
                    ChallengeStatus.STOPPED -> ModuleManager.startChallenges()
                    ChallengeStatus.PAUSED -> chManager.registerChallenges(ModuleManager.getChallenges())
                    ChallengeStatus.RUNNING -> {}
                }
                broadcast(msg("command.timer.resume"))
            }
            "pause" -> {
                if (!status) {
                    sender.sendMessage(msg("command.timer.alreadyOff"))
                    return false
                }
                ModuleManager.setTimerStatus(false)
                if (challenges == ChallengeStatus.RUNNING)
                    chManager.unregisterChallenges(ModuleManager.getChallenges())
                broadcast(msg("command.timer.pause"))
            }
            "setup" -> {
                if (sender !is Player) {
                    sender.sendMessage(msg("command.notPlayer"))
                    return false
                }
                GUIBuilder(sender, GUI.TIMER_SETTINGS, GUIAnimation.WATERFALL_OPEN).custom().open()
            }
            "reset" -> {
                ModuleManager.setTimerStatus(false)
                ModuleManager.setTime(0, 0, 0, 0)
                if (challenges != ChallengeStatus.STOPPED)
                    chManager.stopChallenges(ModuleManager.getChallenges())
                sender.sendMessage(msg("command.timer.reset"))
            }

            else -> {
                sender.sendMessage(msg("command.timer.help", pre = false))
                return false
            }
        }
        return true
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): MutableList<String> {
        val list = ArrayList<String>()

        if (p3.size == 1) {
            list.add("resume")
            list.add("pause")
            list.add("setup")
            list.add("reset")
        }

        return list
    }
}