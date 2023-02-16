package de.miraculixx.api.settings

import kotlinx.serialization.Serializable

@Serializable
data class SettingsData(
    val language: String = "en_US",
)