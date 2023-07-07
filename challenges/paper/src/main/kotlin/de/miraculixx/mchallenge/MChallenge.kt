package de.miraculixx.mchallenge

import de.miraculixx.challenge.api.modules.challenges.ChallengeTags
import de.miraculixx.challenge.api.settings.SettingsData
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.main.KSpigot
import de.miraculixx.kpaper.runnables.taskRun
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mbridge.MUtilsBridge
import de.miraculixx.mbridge.MUtilsModule
import de.miraculixx.mbridge.MUtilsPlatform
import de.miraculixx.mchallenge.commands.ChallengeCommand
import de.miraculixx.mchallenge.commands.CompetitionCommand
import de.miraculixx.mchallenge.commands.CustomRulesCommand
import de.miraculixx.mchallenge.commands.ModuleCommand
import de.miraculixx.mchallenge.commands.utils.*
import de.miraculixx.mchallenge.global.challenges
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.modules.global.DeathListener
import de.miraculixx.mchallenge.modules.global.RuleListener
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mvanilla.extensions.readJsonString
import de.miraculixx.mvanilla.messages.*
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
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
        lateinit var bridgeAPI: MUtilsBridge
    }

    private lateinit var configFile: File
    private lateinit var settingsFile: File
    private lateinit var positionCommand: PositionCommand
    private lateinit var backpackCommand: BackpackCommand

    private var isLoaded = false
    private var isAllowedToStart = true

    override fun startup() {
        CommandAPI.onEnable()

        CoroutineScope(Dispatchers.Default).launch {
            while (!isLoaded) {
                delay(100.milliseconds)
            }
            if (!isAllowedToStart) return@launch

            // Command Setup
            ModuleCommand("mobhunt")
            ModuleCommand("itemhunt")

            // Global Listener Registration
            DeathListener
            //Spectator.register() TODO

            // Run after init & sync
            taskRunLater(1) {
                RuleListener
            }
        }
    }

    override fun load() {
        INSTANCE = this@MChallenge
        consoleAudience = console
        debug = true

        CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(false).silentLogs(true))
        val languages = listOf("en_US", "de_DE", "es_ES").map { it to javaClass.getResourceAsStream("/language/$it.yml") }

        // Define version
        val versionSplit = server.minecraftVersion.split('.')
        majorVersion = versionSplit.getOrNull(1)?.toIntOrNull() ?: 0
        minorVersion = versionSplit.getOrNull(2)?.toIntOrNull() ?: 0

        // Configure Brigadier commands
        ChallengeCommand()
        InvSeeCommand()
        HealCommand()
        ResetCommand()
        HideCommand()
        CustomRulesCommand()
        CompetitionCommand()
        positionCommand = PositionCommand()
        backpackCommand = BackpackCommand()

        // Load configuration
        if (!configFolder.exists()) configFolder.mkdirs()
        configFile = File("${configFolder.path}/settings.json")
        settingsFile = File("${configFolder.path}/config.json")
        ChallengeManager.load(configFile)

        settings = json.decodeFromString<SettingsData>(settingsFile.readJsonString(true))
        debug = settings.debug

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


        // Login with MUtils account
        CoroutineScope(Dispatchers.Default).launch {
            localization = Localization(File("${configFolder.path}/language"), settings.language, languages, challengePrefix)
            Spectator.loadData()

            // Connect Bridge
            bridgeAPI = MUtilsBridge(MUtilsPlatform.PAPER, MUtilsModule.CHALLENGES, server.version, server.port)
            val version = bridgeAPI.versionCheck(description.version.toIntOrNull() ?: 0, File("plugins/update"))
            if (!version) {
                isAllowedToStart = false
                return@launch
            }
            bridgeAPI.login {
                ChallengeManager.stopChallenges()
                challenges.forEach { (challenge, data) ->
                    if (challenge.filter.contains(ChallengeTags.FREE)) return@forEach
                    data.active = false
                }
                consoleAudience.sendMessage(challengePrefix + cmp("Disabled all premium features. Please login with a valid account to continue", cError))
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
