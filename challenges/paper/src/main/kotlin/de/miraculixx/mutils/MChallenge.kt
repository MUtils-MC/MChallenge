package de.miraculixx.mutils

import de.miraculixx.api.settings.SettingsData
import de.miraculixx.api.settings.challenges
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.extensions.pluginManager
import de.miraculixx.kpaper.main.KSpigot
import de.miraculixx.mutils.commands.ChallengeCommand
import de.miraculixx.mutils.commands.InvSeeCommand
import de.miraculixx.mutils.commands.ModuleCommand
import de.miraculixx.mutils.commands.ResetCommand
import de.miraculixx.mutils.extensions.readJsonString
import de.miraculixx.mutils.gui.StorageFilter
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.modules.ChallengeManager
import de.miraculixx.mutils.modules.global.DeathListener
import de.miraculixx.mutils.modules.spectator.Spectator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import java.io.File

class MChallenge : KSpigot() {
    companion object {
        lateinit var INSTANCE: KSpigot
        val configFolder = File("plugins/MUtils/Challenges")
        lateinit var localization: Localization
        var bridgeAPI: MUtilsBridge? = null
    }

    private lateinit var configFile: File
    private lateinit var settingsFile: File

    private var isLoaded = false
    private var isAllowedToStart = true

    override fun startup() {
        getCommand("challenge")?.let {
            ResetCommand()
            val cmd = ChallengeCommand()
            it.setExecutor(cmd)
            it.tabCompleter = cmd
        }
        ModuleCommand("mobhunt")
        InvSeeCommand("invsee")

        DeathListener
//        Spectator.register() TODO
    }

    override fun load() {
        CoroutineScope(Dispatchers.Default).launch {
            INSTANCE = this@MChallenge
            consoleAudience = console
            debug = false

            // Define version
            val versionSplit = server.minecraftVersion.split('.')
            majorVersion = versionSplit.getOrNull(1)?.toIntOrNull() ?: 0
            minorVersion = versionSplit.getOrNull(2)?.toIntOrNull() ?: 0

            // Login with MUtils account
            if (pluginManager.isPluginEnabled("MUtils-Bridge")) {
                bridgeAPI = MUtilsBridge.INSTANCE
                isAllowedToStart = bridgeAPI?.versionCheck(description.version.toIntOrNull() ?: 0, "MChallenge") ?: true
                bridgeAPI?.login {
                    ChallengeManager.stopChallenges()
                    challenges.forEach { (challenge, data) ->
                        if (challenge.filter.contains(StorageFilter.FREE)) return@forEach
                        data.active = false
                    }

                    consoleAudience.sendMessage(prefix + cmp("Disabled all premium features. Please login with a valid account to continue", cError))
                }

            } else {
                consoleAudience.sendMessage(prefix + cmp("MBridge is not installed! MUtils is not able to log you in without it", cError))
                consoleAudience.sendMessage(prefix + cmp("Use /ch bridge-install to automatically install it"))
            }

            // Load configuration
            if (!configFolder.exists()) configFolder.mkdirs()
            configFile = File("${configFolder.path}/settings.json")
            settingsFile = File("${configFolder.path}/config.json")
            ChallengeManager.load(configFile)
            val languages = listOf("en_US", "de_DE", "es_ES").map { it to javaClass.getResourceAsStream("/language/$it.yml") }
            val settings = json.decodeFromString<SettingsData>(settingsFile.readJsonString(true))
            localization = Localization(File("${configFolder.path}/language"), settings.language, languages)
//            Spectator.loadData() TODO

            // Finish loading - starting setup
            isLoaded = true
        }
    }


    override fun shutdown() {
        ChallengeManager.shutDown()
        ChallengeManager.save(configFile)
        Spectator.saveData()
    }
}

val PluginManager by lazy { MChallenge.INSTANCE }

