package de.miraculixx.mchallenge.utils.config

import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mvanilla.extensions.readJsonString
import de.miraculixx.mvanilla.messages.Localization
import de.miraculixx.mvanilla.messages.challengePrefix
import de.miraculixx.mvanilla.messages.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File

object ConfigManager {
    private val settingsFile = File("${MChallenge.configFolder.path}/config.json")
    private val configurableList = mutableListOf<Configurable>()

    val settings = json.decodeFromString<SettingsData>(settingsFile.readJsonString(true))
    val localization: Localization

    fun addConfigurable(configurable: Configurable) {
        configurableList.add(configurable)
        configurable.load()
    }

    fun reset() {
        configurableList.forEach { it.reset() }
    }

    fun save() {
        settingsFile.writeText(json.encodeToString(settings))
        configurableList.forEach { it.save() }
    }

    init {
        val langs = listOf("en_US", "de_DE", "es_ES").map { it to javaClass.getResourceAsStream("/language/$it.yml") }
        localization = Localization(File("${MChallenge.configFolder.path}/language"), settings.language, langs, challengePrefix)
    }
}