package de.miraculixx.mchallenge.commands

import de.miraculixx.kpaper.extensions.bukkit.language
import de.miraculixx.mchallenge.modules.global.CustomRules
import de.miraculixx.mchallenge.modules.global.RuleListener
import de.miraculixx.mcommons.extensions.enumOf
import de.miraculixx.mcommons.text.msg
import de.miraculixx.mcommons.text.plus
import de.miraculixx.mcommons.text.prefix
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.booleanArgument
import dev.jorel.commandapi.kotlindsl.commandTree

class CustomRulesCommand {
    val command = commandTree("rule") {
        withPermission("command.rule")
        argument(StringArgument("rule").replaceSuggestions(ArgumentSuggestions.strings(CustomRules.entries.map { it.name.lowercase() }))) {
            booleanArgument("active") {
                anyExecutor { sender, args ->
                    val stringRule = args[0] as String
                    val active = args[1] as Boolean
                    val rule = enumOf<CustomRules>(stringRule.uppercase())
                    val locale = sender.language()
                    if (rule == null) {
                        sender.sendMessage(prefix + locale.msg("command.noRule"))
                        return@anyExecutor
                    }
                    RuleListener.updateRule(rule, active)
                    sender.sendMessage(prefix + locale.msg("command.updateRule", listOf(stringRule, active.toString())))
                }
            }
        }
    }
}