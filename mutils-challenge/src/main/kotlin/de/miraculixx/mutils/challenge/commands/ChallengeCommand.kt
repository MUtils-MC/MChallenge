package de.miraculixx.mutils.challenge.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.miraculixx.kpaper.commands.command
import de.miraculixx.kpaper.commands.literal
import de.miraculixx.kpaper.commands.runs
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.mutils.challenge.modules.ChallengeManager
import de.miraculixx.mutils.utils.enums.challenges.ChallengeStatus
import de.miraculixx.mutils.utils.gui.data.InventoryManager
import de.miraculixx.mutils.utils.messages.msg
import net.minecraft.commands.CommandSourceStack

class ChallengeCommand {
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
}