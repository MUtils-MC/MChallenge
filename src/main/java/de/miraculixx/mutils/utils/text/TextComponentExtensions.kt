package de.miraculixx.mutils.utils.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

fun Component.decorate(bold: Boolean? = null, italic: Boolean? = null, strikethrough: Boolean? = null, underlined: Boolean? = null): Component {
    var finalComponent = this
    if (bold != null) finalComponent = finalComponent.decoration(TextDecoration.BOLD, bold)
    if (italic != null) finalComponent = finalComponent.decoration(TextDecoration.ITALIC, italic)
    if (strikethrough != null) finalComponent = finalComponent.decoration(TextDecoration.STRIKETHROUGH, strikethrough)
    if (underlined != null) finalComponent = finalComponent.decoration(TextDecoration.UNDERLINED, underlined)
    return finalComponent
}

fun Component.lore(): Component {
    return decoration(TextDecoration.ITALIC, false)
}

fun emptyComponent(): Component {
    return Component.text(" ")
}

fun cmp(text: String, color: TextColor = cBase, bold: Boolean = false, italic: Boolean = false, strikethrough: Boolean = false, underlined: Boolean = false): Component {
    return Component.text(text).color(color)
        .decorations(mapOf(
            TextDecoration.BOLD to TextDecoration.State.byBoolean(bold),
            TextDecoration.ITALIC to TextDecoration.State.byBoolean(italic),
            TextDecoration.STRIKETHROUGH to TextDecoration.State.byBoolean(strikethrough),
            TextDecoration.UNDERLINED to TextDecoration.State.byBoolean(underlined)
        ))
}

operator fun Component.plus(other: Component): Component {
    return append(other)
}