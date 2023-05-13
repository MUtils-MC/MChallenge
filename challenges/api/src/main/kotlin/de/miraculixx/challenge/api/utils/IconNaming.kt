package de.miraculixx.challenge.api.utils

import de.miraculixx.challenge.api.settings.ComponentSerializer
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

/**
 * Define the name and lore for an [Icon]
 * @param name The name in component style
 * @param lore The lore in component style (list)
 */
@Serializable
data class IconNaming(
    val name: @Serializable(with = ComponentSerializer::class) Component,
    val lore: List<@Serializable(with = ComponentSerializer::class) Component>,
)
