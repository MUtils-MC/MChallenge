package de.miraculixx.mchallenge.utils.config

import de.miraculixx.mcommons.serializer.LocaleSerializer
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class SettingsData(
    var debug: Boolean = false,
    var language: @Serializable(with = LocaleSerializer::class) Locale = Locale.ENGLISH,
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