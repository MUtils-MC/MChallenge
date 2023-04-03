package de.miraculixx.mutils

import de.miraculixx.api.MChallengeAPI
import de.miraculixx.api.MUtilsBridge
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.main.KSpigot
import de.miraculixx.mutils.command.HelperCommand
import de.miraculixx.mutils.command.TimerCommand
import de.miraculixx.mutils.data.Settings
import de.miraculixx.mvanilla.extensions.readJsonString
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.module.TimerManager
import de.miraculixx.mutils.utils.registerCommand
import de.miraculixx.mvanilla.messages.*
import kotlinx.serialization.decodeFromString
import org.bukkit.Bukkit
import java.io.File

class MTimer : KSpigot() {
    companion object {
        lateinit var INSTANCE: KSpigot
        val configFolder = File("plugins/MUtils/Timer")
        lateinit var localization: Localization
        var chAPI: MChallengeAPI? = null
        var bridgeAPI: MUtilsBridge? = null
    }

    override fun startup() {
        INSTANCE = this
        consoleAudience = console
        debug = true

        val versionSplit = server.minecraftVersion.split('.')
        majorVersion = versionSplit.getOrNull(1)?.toIntOrNull() ?: 0
        minorVersion = versionSplit.getOrNull(2)?.toIntOrNull() ?: 0

        if (!configFolder.exists()) configFolder.mkdirs()
        val settings = json.decodeFromString<Settings>(File("${configFolder.path}/settings.json").readJsonString(true))
        val languages = listOf("en_US", "de_DE", "es_ES").map { it to javaClass.getResourceAsStream("/language/$it.yml") }
        localization = Localization(File("${configFolder.path}/language"), settings.language, languages)

        registerCommand("timer", TimerCommand(false))
        registerCommand("ptimer", TimerCommand(true))
        registerCommand("colorful", HelperCommand())

        val chPlugin = Bukkit.getPluginManager().getPlugin("MUtils-Challenges")
        if (chPlugin != null) chAPI = MChallengeAPI.instance
        val bridgePlugin = Bukkit.getPluginManager().getPlugin("MUtils-Bridge")
        if (bridgePlugin != null) bridgeAPI = MUtilsBridge.INSTANCE

        TimerManager.load(configFolder)
    }

    override fun shutdown() {
        TimerManager.save(configFolder)
    }
}

val PluginManager by lazy { MTimer.INSTANCE }