package de.miraculixx.mchallenge.commands

import de.miraculixx.mchallenge.modules.global.CustomRules
import de.miraculixx.mchallenge.modules.global.RuleListener
import de.miraculixx.mvanilla.extensions.enumOf
import de.miraculixx.mvanilla.messages.msg
import de.miraculixx.mvanilla.messages.plus
import de.miraculixx.mvanilla.messages.prefix
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.booleanArgument
import dev.jorel.commandapi.kotlindsl.commandTree

class CustomRulesCommand {
    val command = commandTree("rule", { it.hasPermission("command.rule") }) {
        argument(StringArgument("rule").replaceSuggestions(ArgumentSuggestions.strings(CustomRules.values().map { it.name.lowercase() }))) {
            booleanArgument("active") {
                anyExecutor { sender, args ->
                    val stringRule = args[0] as String
                    val active = args[1] as Boolean
                    val rule = enumOf<CustomRules>(stringRule.uppercase())
                    if (rule == null) {
                        sender.sendMessage(prefix + msg("command.noRule"))
                        return@anyExecutor
                    }
                    RuleListener.updateRule(rule, active)
                    sender.sendMessage(prefix + msg("command.updateRule", listOf(stringRule, active.toString())))
                }
            }
        }
    }
}