package de.miraculixx.mutils.utils.settings

import kotlinx.serialization.Serializable

@Serializable
data class SettingsData(
    val language: String = "en_US",
)