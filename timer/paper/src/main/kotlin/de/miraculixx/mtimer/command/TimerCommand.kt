package de.miraculixx.mtimer.command

import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.mtimer.MTimer
import de.miraculixx.mtimer.gui.actions.GUIOverview
import de.miraculixx.mtimer.gui.buildInventory
import de.miraculixx.mtimer.gui.items.ItemsOverview
import de.miraculixx.mtimer.module.PaperTimer
import de.miraculixx.mtimer.module.load
import de.miraculixx.mtimer.vanilla.data.TimerGUI
import de.miraculixx.mtimer.vanilla.module.Timer
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mvanilla.extensions.soundDisable
import de.miraculixx.mvanilla.extensions.soundEnable
import de.miraculixx.mvanilla.messages.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import kotlin.time.Duration

class TimerCommand(private val isPersonal: Boolean) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            if (sender is Player) openSetup(sender)
            else sender.sendMessage(prefix + msg("command.help"))
            return true
        } else if (args.size > 1) {
            sender.sendMessage(prefix + msg("command.help"))
            return false
        }

        val timer = getTimer(sender)
        val running = timer.running
        when (args[0].lowercase()) {
            "resume" -> if (running) {
                sender.sendMessage(prefix + msg("command.alreadyOn"))
                return false
            } else {
                timer.running = true
                sender.soundEnable()
                val msg = prefix + msg("command.resume", listOf(sender.name))
                if (isPersonal) sender.sendMessage(msg) else broadcast(msg)
            }

            "pause" -> if (!running) {
                sender.sendMessage(prefix + msg("command.alreadyOff"))
                return false
            } else {
                timer.running = false
                sender.soundDisable()
                val msg = prefix + msg("command.pause", listOf(sender.name))
                if (isPersonal) sender.sendMessage(msg) else broadcast(msg)
            }

            "setup" -> if (sender is Player) openSetup(sender)
            else sender.sendMessage(msg("command.notPlayer"))

            "reset" -> {
                timer.running = false
                timer.time = Duration.ZERO
                sender.soundDisable()
                sender.sendMessage(prefix + msg("command.reset"))
            }

            "config" -> if (isPersonal && !sender.hasPermission("mutils.command.timer-config")) {
                sender.sendMessage(prefix + msg("command.help"))
                return false
            } else when (args.getOrNull(1)?.lowercase()) {
                "save" -> TimerManager.save(MTimer.configFolder)
                "load" -> TimerManager.load(MTimer.configFolder)
                else -> sender.sendMessage(prefix + msg("command.help"))
            }

            "data/language" -> {
                if (!isPersonal && sender.hasPermission("mutils.command.timer-config")) {
                    sender.sendMessage(prefix + msg("command.help"))
                    return false
                }
                val key = args.getOrNull(1)
                if (key == null) {
                    sender.sendMessage(prefix + cmp("Please provide a valid language key!", cError))
                } else if (MTimer.localization.setLanguage(key)) {
                    sender.sendMessage(prefix + msg("command.language"))
                }
            }

            else -> sender.sendMessage(prefix + msg("command.help"))
        }
        return true
    }

    private fun openSetup(player: Player) {
        val id = if (isPersonal) player.uniqueId.toString() else "TIMER_GLOBAL"
        TimerGUI.OVERVIEW.buildInventory(player, id, ItemsOverview(getTimer(player), isPersonal), GUIOverview(isPersonal))
    }

    private fun getTimer(sender: CommandSender): Timer {
        val timer = if (isPersonal && sender is Player) TimerManager.getPersonalTimer(sender.uniqueId) else TimerManager.globalTimer
        return if (timer == null) {
            if (debug) consoleAudience.sendMessage(prefix + cmp("Creating new personal timer for ${sender.name}"))
            val newTimer = PaperTimer(true, (sender as Player).uniqueId, null)
            newTimer.design = TimerManager.globalTimer.design
            newTimer.visible = false
            TimerManager.addPersonalTimer(sender.uniqueId, newTimer)
            newTimer
        } else timer
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String> {
        return buildList {
            when (args?.size ?: 0) {
                0, 1 -> {
                    addAll(listOf("resume", "pause", "setup", "reset"))
                    if (!isPersonal && sender.hasPermission("mutils.command.timer-config")) {
                        val input = args?.getOrNull(0)
                        if (input?.startsWith('c') == true) add("config")
                        else if (input?.startsWith('l') == true) add("data/language")
                    }
                }

                2 -> when (args?.getOrNull(0)) {
                    "config" -> addAll(listOf("save", "load"))
                    "data/language" -> addAll(MTimer.localization.getLoadedKeys())
                }
            }
        }.filter { it.startsWith(args?.lastOrNull() ?: "", ignoreCase = true) }.toMutableList()
    }
}