@file:Suppress("unused")

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
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.kotlindsl.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import kotlin.time.Duration

class TimerCommand {
    private val langFolder = File(MTimer.configFolder, "language")
    private val global = commandTree("timer") {
        withPermission("mutils.command.timer")
        playerExecutor { player, _ -> openSetup(player, false) }
        literalArgument("setup") {
            playerExecutor { player, _ -> openSetup(player, false) }
        }

        literalArgument("resume") {
            anyExecutor { sender, _ -> sender.resume(false) }
        }

        literalArgument("pause") {
            anyExecutor { sender, _ -> sender.pause(false) }
        }

        literalArgument("reset") {
            anyExecutor { sender, _ -> sender.reset(false) }
        }

        literalArgument("config") {
            withPermission("mutils.command.timer-config")
            literalArgument("save") {
                anyExecutor { sender, _ ->
                    TimerManager.save(MTimer.configFolder)
                    sender.sendMessage(prefix + cmp("Saved all temporary data to disk"))
                }
            }
            literalArgument("load") {
                anyExecutor { sender, _ ->
                    TimerManager.load(MTimer.configFolder)
                    sender.sendMessage(prefix + cmp("Reloaded all temporary data from disk"))
                }
            }
            literalArgument("language") {
                stringArgument("name") {
                    replaceSuggestions(ArgumentSuggestions.stringCollection {
                        langFolder.listFiles()?.map { it.nameWithoutExtension } ?: emptyList()
                    })
                    anyExecutor { sender, args ->
                        val key = args[0] as String
                        if (MTimer.localization.setLanguage(key)) sender.sendMessage(prefix + msg("command.language"))
                        else sender.sendMessage(prefix + cmp("Invalid language file! Copy an existing file to start editing"))
                    }
                }
            }
        }

        literalArgument("time") {
            literalArgument("set") {
                greedyStringArgument("time") {
                    anyExecutor { sender, args -> sender.setTime(false, args[0] as String, false) }
                }
            }
            literalArgument("add") {
                greedyStringArgument("time") {
                    anyExecutor { sender, args -> sender.setTime(false, args[0] as String, true) }
                }
            }
        }
    }

    private val personal = commandTree("ptimer") {
        withPermission("mutils.command.ptimer")
        playerExecutor { player, _ -> openSetup(player, true) }
        literalArgument("setup") {
            playerExecutor { player, _ -> openSetup(player, true) }
            entitySelectorArgumentOnePlayer("target") {
                //Only the GUI needs an external execute form others command
                //because 'execute as <target> run ...' would open it for the target player
                withPermission("mutils.command.ptimer-others")
                playerExecutor { player, args ->
                    val target = args[0] as Player
                    openSetup(player, true, target)
                }
            }
        }

        literalArgument("resume") {
            playerExecutor { sender, _ -> sender.resume(true) }
        }

        literalArgument("pause") {
            playerExecutor { sender, _ -> sender.pause(true) }
        }

        literalArgument("reset") {
            playerExecutor { sender, _ -> sender.reset(true) }
        }

        literalArgument("time") {
            literalArgument("set") {
                greedyStringArgument("time") {
                    anyExecutor { sender, args -> sender.setTime(true, args[0] as String, false) }
                }
            }
            literalArgument("add") {
                greedyStringArgument("time") {
                    anyExecutor { sender, args -> sender.setTime(true, args[0] as String, true) }
                }
            }
        }
    }

    private fun CommandSender.setTime(isPersonal: Boolean, string: String, relative: Boolean) {
        val timer = getTimer(this, isPersonal)
        val time = try {
            Duration.parse(string)
        } catch (_: IllegalArgumentException) {
            sendMessage(prefix + cmp("Please enter a value like '5m 3s' (valid times: s,m,h,d). Negative and floating numbers are allowed", cError))
            return
        }
        if (relative) timer.time += time else timer.time = time
        soundEnable()
        sendMessage(prefix + cmp("Changed time to ${timer.time}", cSuccess))
    }

    private fun CommandSender.reset(isPersonal: Boolean) {
        val timer = getTimer(this, isPersonal)
        timer.running = false
        timer.time = Duration.ZERO
        soundDisable()
        sendMessage(prefix + msg("command.reset"))
    }

    private fun CommandSender.pause(isPersonal: Boolean) {
        val timer = getTimer(this, isPersonal)
        val running = timer.running
        if (!running) {
            sendMessage(prefix + msg("command.alreadyOff"))
            return
        } else {
            timer.running = false
            soundDisable()
            val msg = prefix + msg("command.pause", listOf(name))
            if (isPersonal) sendMessage(msg) else broadcast(msg)
        }
    }

    private fun CommandSender.resume(isPersonal: Boolean) {
        val timer = getTimer(this, isPersonal)
        val running = timer.running
        if (running) sendMessage(prefix + msg("command.alreadyOn"))
        else {
            timer.running = true
            soundEnable()
            val msg = prefix + msg("command.resume", listOf(name))
            if (isPersonal) sendMessage(msg) else broadcast(msg)
        }
    }

    private fun openSetup(player: Player, isPersonal: Boolean, target: Player = player) {
        val id = if (isPersonal) target.uniqueId.toString() else "TIMER_GLOBAL"
        TimerGUI.OVERVIEW.buildInventory(
            player,
            id,
            ItemsOverview(getTimer(target, isPersonal), isPersonal),
            GUIOverview(isPersonal)
        )
    }

    private fun getTimer(sender: CommandSender, isPersonal: Boolean): Timer {
        val timer =
            if (isPersonal && sender is Player) TimerManager.getPersonalTimer(sender.uniqueId) else TimerManager.globalTimer
        return if (timer == null) {
            if (debug) consoleAudience.sendMessage(prefix + cmp("Creating new personal timer for ${sender.name}"))
            val newTimer = PaperTimer(true, (sender as Player).uniqueId, null)
            newTimer.design = TimerManager.globalTimer.design
            newTimer.visible = false
            TimerManager.addPersonalTimer(sender.uniqueId, newTimer)
            newTimer
        } else timer
    }
}