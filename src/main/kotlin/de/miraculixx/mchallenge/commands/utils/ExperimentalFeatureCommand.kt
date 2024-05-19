package de.miraculixx.mchallenge.commands.utils

import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.server
import de.miraculixx.mcommons.text.*
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.textArgument
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.world.flag.FeatureFlagRegistry
import net.minecraft.world.flag.FeatureFlagSet
import org.bukkit.craftbukkit.CraftServer

object ExperimentalFeatureCommand {
    val enabledFeatures = mutableSetOf<String>()

    private val command = commandTree("experimental-features") {
        withPermission("command.experimental")

        literalArgument("enable") {
            textArgument("feature") {
                replaceSuggestions(ArgumentSuggestions.strings("update_1_21", "bundle", "trade_rebalance"))
                anyExecutor { sender, args ->
                    val feature = args[0] as String

                    try {
                        val worldData = (server as CraftServer).handle.server.worldData
                        val newFeature = FeatureFlagRegistry.Builder("main").createVanilla(feature)
                        worldData.dataConfiguration = worldData.dataConfiguration.expandFeatures(FeatureFlagSet.of(newFeature))
                    } catch (e: Exception) {
                        sender.sendMessage(prefix + cmp("Something went wrong! Error: ${e.message}"))
                    }

                    enabledFeatures.add(feature)
                    onlinePlayers.forEach {
                        it.kick(
                            cmp("New Feature Enabled", cHighlight) +
                                    cmp("\n                                            ", NamedTextColor.WHITE, strikethrough = true) +
                                    cmp("\nPlease rejoin to access all new features") +
                                    cmp("\nNew Experiment: ") + cmp(feature, cMark)
                        )
                    }
                }
            }
        }
    }
}