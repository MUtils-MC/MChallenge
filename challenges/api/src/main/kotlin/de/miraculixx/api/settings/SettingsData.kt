package de.miraculixx.api.settings

import kotlinx.serialization.Serializable

@Serializable
data class SettingsData(
    var language: String = "en_US",
    var reset: Boolean = false,
    val worlds: MutableSet<String> = mutableSetOf(),
)