package de.miraculixx.mchallenge.modules.challenges.interfaces

import de.miraculixx.mchallenge.utils.Command
import de.miraculixx.mchallenge.utils.bc
import de.miraculixx.mcommons.text.prefix

interface ChallengeCommand {
    val command: Command

    fun registerCommand() {
        bc(prefix, "command.registered", listOf(command.name))
        command.register()
    }

    fun unregisterCommand() {
        command.unregister()
    }
}