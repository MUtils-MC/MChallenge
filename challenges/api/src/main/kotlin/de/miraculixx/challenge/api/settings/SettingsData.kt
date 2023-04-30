package de.miraculixx.challenge.api.settings

import kotlinx.serialization.Serializable

@Serializable
data class SettingsData(
    var debug: Boolean = false,
    var language: String = "en_US",
    var reset: Boolean = false,
    val worlds: MutableSet<String> = mutableSetOf(),
    val iReallyDontWantAnyPremiumFeatures: Boolean = false
)