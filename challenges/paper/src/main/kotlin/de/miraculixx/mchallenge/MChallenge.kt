package de.miraculixx.mchallenge

import de.miraculixx.api.settings.SettingsData
import de.miraculixx.api.settings.challenges
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.extensions.pluginManager
import de.miraculixx.kpaper.main.KSpigot
import de.miraculixx.mchallenge.commands.ChallengeCommand
import de.miraculixx.mchallenge.commands.ModuleCommand
import de.miraculixx.mchallenge.commands.utils.*
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.modules.global.DeathListener
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.api.MUtilsBridge
import de.miraculixx.mvanilla.extensions.readJsonString
import de.miraculixx.mvanilla.gui.StorageFilter
import de.miraculixx.mvanilla.messages.*
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File
import java.nio.file.Paths
import kotlin.time.Duration.Companion.milliseconds


class MChallenge : KSpigot() {
    companion object {
        lateinit var INSTANCE: KSpigot
        val configFolder = File("plugins/MUtils/Challenges")
        lateinit var localization: Localization
        lateinit var settings: SettingsData

        var bridgeAPI: MUtilsBridge? = null
    }

    private lateinit var configFile: File
    private lateinit var settingsFile: File
    private lateinit var positionCommand: PositionCommand
    private lateinit var backpackCommand: BackpackCommand

    private var isLoaded = false
    private var isAllowedToStart = true

    override fun startup() {
        CommandAPI.onEnable(this)

        CoroutineScope(Dispatchers.Default).launch {
            while (!isLoaded) {
                delay(100.milliseconds)
            }
            if (!isAllowedToStart) return@launch

            // Command Setup
            getCommand("challenge")?.let {
                val cmd = ChallengeCommand()
                it.setExecutor(cmd)
                it.tabCompleter = cmd
            }
            ModuleCommand("mobhunt")
            ModuleCommand("itemhunt")
            InvSeeCommand()
            HealCommand()
            ResetCommand()
            HideCommand()
            positionCommand = PositionCommand()
            backpackCommand = BackpackCommand()

            // Global Listener Registration
            DeathListener
//        Spectator.register() TODO
        }
    }

    override fun load() {
        INSTANCE = this@MChallenge
        consoleAudience = console
        debug = false

        CommandAPI.onLoad(CommandAPIConfig().verboseOutput(debug).silentLogs(!debug))
        val languages = listOf("en_US", "de_DE", "es_ES").map { it to javaClass.getResourceAsStream("/language/$it.yml") }

        CoroutineScope(Dispatchers.Default).launch {
            // Define version
            val versionSplit = server.minecraftVersion.split('.')
            majorVersion = versionSplit.getOrNull(1)?.toIntOrNull() ?: 0
            minorVersion = versionSplit.getOrNull(2)?.toIntOrNull() ?: 0

            // Login with MUtils account
            if (pluginManager.isPluginEnabled("MUtils-Bridge")) {
                bridgeAPI = MUtilsBridge.Companion.INSTANCE
                isAllowedToStart = bridgeAPI?.versionCheck(description.version.toIntOrNull() ?: 0, "MUtils-Challenge") ?: true
                bridgeAPI?.login {
                    ChallengeManager.stopChallenges()
                    challenges.forEach { (challenge, data) ->
                        if (challenge.filter.contains(StorageFilter.FREE)) return@forEach
                        data.active = false
                    }

                    consoleAudience.sendMessage(exactPrefix + cmp("Disabled all premium features. Please login with a valid account to continue", cError))
                }

            } else {
                consoleAudience.sendMessage(exactPrefix + cmp("MBridge is not installed! MUtils is not able to log you in without it", cError))
                consoleAudience.sendMessage(exactPrefix + cmp("Use /ch bridge-install to automatically install it"))
            }

            // Load configuration
            if (!configFolder.exists()) configFolder.mkdirs()
            configFile = File("${configFolder.path}/settings.json")
            settingsFile = File("${configFolder.path}/config.json")
            ChallengeManager.load(configFile)

            settings = json.decodeFromString<SettingsData>(settingsFile.readJsonString(true))
            localization = Localization(File("${configFolder.path}/language"), settings.language, languages)
            Spectator.loadData()

            // Reset World
            if (settings.reset) {
                console.sendMessage(prefix + cmp("Delete loaded worlds..."))
                settings.worlds.forEach {
                    val currentRelativePath = Paths.get(it)
                    val path = currentRelativePath.toAbsolutePath().toString()
                    console.sendMessage(prefix + cmp("World Path: $path"))
                    File(path).listFiles()?.forEach { file ->
                        file.deleteRecursively()
                        File("$path/playerdata").mkdirs()
                    }
                }
                settings.reset = false
                settings.worlds.clear()
            }

            // Finish loading - starting setup
            isLoaded = true
        }
    }


    override fun shutdown() {
        CommandAPI.onDisable()
        ChallengeManager.shutDown()
        ChallengeManager.save(configFile)
//        Spectator.saveData()

        settingsFile.writeText(json.encodeToString(settings))
        positionCommand.saveFile()
        backpackCommand.saveFile()
    }
}

val PluginManager by lazy { MChallenge.INSTANCE }
