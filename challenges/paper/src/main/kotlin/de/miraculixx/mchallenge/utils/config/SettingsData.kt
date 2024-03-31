package de.miraculixx.mchallenge.utils.config

import kotlinx.serialization.Serializable

@Serializable
data class SettingsData(
    var debug: Boolean = false,
    var language: String = "en_US",
    val gui: GuiSettings = GuiSettings(),
    var reset: Boolean = false,
    val worlds: MutableSet<String> = mutableSetOf()
)

@Serializable
data class GuiSettings(
    var compact: Boolean = false,
    var itemTheme: String = "default",
    var textTheme: String = "default"
)