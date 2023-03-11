@file:Suppress("MemberVisibilityCanBePrivate", "Unused")

package de.miraculixx.kpaper.chat

import de.miraculixx.kpaper.chat.LiteralTextBuilder
import de.miraculixx.kpaper.chat.literalText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import org.bukkit.command.CommandSender

fun CommandSender.sendMessage(vararg components: Component) {
    this.sendMessage(Component.join(JoinConfiguration.separator(Component.newline()), *components))
}

/**
 * Opens a [LiteralTextBuilder].
 *
 * @param baseText the text you want to begin with, it is okay to let this empty
 * @param builder the builder which can be used to set the style and add child text components
 */
inline fun CommandSender.sendText(
    baseText: String = "",
    crossinline builder: LiteralTextBuilder.() -> Unit = { }
) = this.sendMessage(literalText(baseText, builder))
