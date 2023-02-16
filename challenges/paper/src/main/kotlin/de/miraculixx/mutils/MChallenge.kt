package de.miraculixx.mutils

import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.main.KSpigot
import de.miraculixx.mutils.api.MUtilsAPI
import de.miraculixx.mutils.commands.ChallengeCommand
import de.miraculixx.mutils.commands.ModuleCommand
import de.miraculixx.mutils.commands.ResetCommand
import de.miraculixx.mutils.enums.Challenges
import de.miraculixx.mutils.extensions.enumOf
import de.miraculixx.mutils.extensions.readJsonString
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.modules.ChallengeManager
import de.miraculixx.mutils.modules.global.DeathListener
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.utils.cotm
import de.miraculixx.mutils.utils.settings.SettingsData
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
        lateinit var api: MUtilsAPI
    }

    private lateinit var configFile: File
    private lateinit var settingsFile: File

    override fun startup() {
        getCommand("challenge")!!.let {
            ResetCommand()
            val cmd = ChallengeCommand()
            it.setExecutor(cmd)
            it.tabCompleter = cmd
        }
        ModuleCommand("mobhunt")

        DeathListener
//        Spectator.register()
    }

    override fun load() {
        INSTANCE = this
        consoleAudience = console
        debug = false

        // Define version
        val versionSplit = server.minecraftVersion.split('.')
        majorVersion = versionSplit.getOrNull(1)?.toIntOrNull() ?: 0
        minorVersion = versionSplit.getOrNull(2)?.toIntOrNull() ?: 0

        // Load configuration
        if (!configFolder.exists()) configFolder.mkdirs()
        configFile = File("${configFolder.path}/settings.json")
        settingsFile = File("${configFolder.path}/config.json")
        ChallengeManager.load(configFile)
        val languages = listOf("en_US").map { it to javaClass.getResourceAsStream("/language/$it.yml") }
        val settings = json.decodeFromString<SettingsData>(settingsFile.readJsonString(true))
        localization = Localization(File("${configFolder.path}/language"), settings.language, languages)
//        Spectator.loadData()

        // Connect to API
        CoroutineScope(Dispatchers.Default).launch {
            api = MUtilsAPI("challenges", description.version.toInt(), configFolder, "${server.ip}:${server.port}")
            cotm = enumOf<Challenges>(api.getCOTM()) ?: Challenges.FLY
        }
    }


    override fun shutdown() {
        ChallengeManager.shutDown()
        ChallengeManager.save(configFile)
//        Spectator.saveData()
    }
}

val PluginManager by lazy { MChallenge.INSTANCE }

