package de.miraculixx.mchallenge.utils.config

import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mcommons.text.Localization
import kotlinx.serialization.encodeToString
import java.io.File
import java.util.*

object ConfigManager {
    private val settingsFile = File("${MChallenge.configFolder.path}/config.json")
    private val configurableList = mutableListOf<Configurable>()

    val settings = settingsFile.loadConfig(SettingsData())
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
        val langs = listOf(Locale.ENGLISH, Locale.GERMAN, Locale.forLanguageTag("es")).map { it to javaClass.getResourceAsStream("/language/$it.yml") }
        localization = Localization(File("${MChallenge.configFolder.path}/language"), settings.language, langs)
    }
}