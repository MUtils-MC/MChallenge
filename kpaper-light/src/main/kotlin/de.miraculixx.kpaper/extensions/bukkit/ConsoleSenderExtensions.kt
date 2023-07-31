@file:Suppress("unused")

package de.miraculixx.kpaper.extensions.bukkit

import de.miraculixx.kpaper.main.PluginInstance
import de.miraculixx.mvanilla.extensions.native
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

/** @see printColoredPrefix */
fun CommandSender.print(text: String, plugin: Plugin? = PluginInstance) =
    printColoredPrefix(text, NamedTextColor.WHITE, plugin?.name ?: "INFO", NamedTextColor.GRAY)

/** @see printColoredPrefix */
fun CommandSender.info(text: String, plugin: Plugin? = PluginInstance) =
    printColoredPrefix(text, NamedTextColor.WHITE, plugin?.name ?: "INFO", NamedTextColor.DARK_AQUA)

/** @see printColoredPrefix */
fun CommandSender.success(text: String, plugin: Plugin? = PluginInstance) =
    printColoredPrefix(text, NamedTextColor.GREEN, plugin?.name ?: "SUCCESS", NamedTextColor.DARK_AQUA)

/** @see printColoredPrefix */
fun CommandSender.warn(text: String, plugin: Plugin? = PluginInstance) =
    printColoredPrefix(text, NamedTextColor.WHITE, plugin?.name?.plus(" - WARN") ?: "WARN", NamedTextColor.YELLOW)

/** @see printColoredPrefix */
fun CommandSender.error(text: String, plugin: Plugin? = PluginInstance) =
    printColoredPrefix(text, NamedTextColor.RED, plugin?.name?.plus(" - ERROR") ?: "ERROR", NamedTextColor.DARK_RED)

/**
 * Sends the given message and adds the given prefix with the given color to it.
 */
fun CommandSender.printColoredPrefix(text: String, textColor: TextColor, prefix: String, prefixColor: TextColor) =
    sendMessage(Component.text(prefix).color(prefixColor).append(Component.text(text).color(textColor)).native())

/**
 * Dispatches the command given by [commandLine].
 *
 * @param commandLine the command without a leading /
 */
fun CommandSender.dispatchCommand(commandLine: String) =
    Bukkit.dispatchCommand(this, commandLine)
