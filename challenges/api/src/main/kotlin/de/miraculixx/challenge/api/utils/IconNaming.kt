package de.miraculixx.challenge.api.utils

import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

/**
 * Define the name and lore for an [Icon]
 * @param name The name in component style
 * @param lore The lore in component style (list)
 */
@Serializable
data class IconNaming(
    val name: Component,
    val lore: List<Component>,
)
