package de.miraculixx.mutils.commands

import de.miraculixx.kpaper.commands.command
import de.miraculixx.kpaper.commands.literal
import de.miraculixx.kpaper.commands.runs
import de.miraculixx.kpaper.extensions.broadcast
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class ChallengeCommand: TabExecutor {
    val challenge = command("challenge") {
        commandLogic("challenge")
    }
    val ch = command("ch") {
        commandLogic("ch")
    }

    private fun LiteralArgumentBuilder<CommandSourceStack>.commandLogic(prefix: String) {
        literal("start") {
            runs {
                val audience = sender.bukkitSender
                if (ChallengeManager.status == ChallengeStatus.RUNNING) {
                    audience.sendMessage(prefix + msg("command.challenge.alreadyOn"))
                    return@runs
                }

                if (ChallengeManager.startChallenges()) {
                    ChallengeManager.status = ChallengeStatus.RUNNING
                    broadcast(msg("command.challenge.start", listOf(sender.textName)))
                } else audience.sendMessage(prefix + msg("command.challenge.failed"))
            }
        }

        literal("stop") {
            runs {
                if (ChallengeManager.stopChallenges()) {
                    ChallengeManager.status = ChallengeStatus.STOPPED
                    broadcast((msg("command.challenge.stop", listOf(sender.textName))))
                } else sender.bukkitSender.sendMessage(prefix + msg("command.challenge.alreadyOff"))
            }
        }

        literal("pause") {
            runs {
                if (ChallengeManager.unregisterChallenges()) {
                    ChallengeManager.status = ChallengeStatus.PAUSED
                    broadcast(msg("command.challenge.pause", listOf(sender.textName)))
                } else sender.bukkitSender.sendMessage(prefix + msg("command.challenge.alreadyOff"))
            }
        }

        literal("resume") {
            runs {
                if (ChallengeManager.registerChallenges()) {
                    ChallengeManager.status = ChallengeStatus.RUNNING
                    broadcast(msg("command.challenge.continue", listOf(sender.textName)))
                } else sender.bukkitSender.sendMessage(prefix + msg("command.challenge.alreadyOff"))
            }
        }

        runs {
            if (!sender.isPlayer) {
                sender.bukkitSender.sendMessage(prefix + msg("command.noPlayer"))
                return@runs
            }
            InventoryManager.scrollBuilder(player.uniqueId.toString()) {
                title = msg("gui.challenge.title")
                player = sender.player
                content
            }
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String> {
        return buildList {
            when (args?.size ?: 0) {
                0, 1 -> addAll(listOf("stop", "start", "pause", "resume"))
            }
        }.toMutableList()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (args?.isEmpty() == true) {
            if (sender is Player) InventoryMa
            return true
        }

        return true
    }
}