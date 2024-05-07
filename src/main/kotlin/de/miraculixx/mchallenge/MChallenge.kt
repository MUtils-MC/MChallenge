package de.miraculixx.mchallenge

import de.miraculixx.challenge.api.modules.challenges.ChallengeTags
import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.main.KPaper
import de.miraculixx.kpaper.runnables.taskRunLater
import de.miraculixx.mbridge.MUtilsBridge
import de.miraculixx.mbridge.MUtilsBridge.Companion.debug
import de.miraculixx.mbridge.data.MUtilsModule
import de.miraculixx.mbridge.data.MUtilsPlatform
import de.miraculixx.mchallenge.commands.ChallengeCommand
import de.miraculixx.mchallenge.commands.CompetitionCommand
import de.miraculixx.mchallenge.commands.CustomRulesCommand
import de.miraculixx.mchallenge.commands.ModuleCommand
import de.miraculixx.mchallenge.commands.utils.*
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.modules.challenges.challenges
import de.miraculixx.mchallenge.modules.global.DeathListener
import de.miraculixx.mchallenge.modules.global.RuleListener
import de.miraculixx.mchallenge.modules.packs.ResourcePacks
import de.miraculixx.mchallenge.modules.spectator.Spectator
import de.miraculixx.mchallenge.utils.config.ConfigManager
import de.miraculixx.mcommons.majorVersion
import de.miraculixx.mcommons.minorVersion
import de.miraculixx.mcommons.text.*
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Paths
import kotlin.time.Duration.Companion.milliseconds


class MChallenge : KPaper() {
    companion object {
        lateinit var INSTANCE: KPaper
        val configFolder = File("plugins/MUtils/Challenges")
        lateinit var bridgeAPI: MUtilsBridge
    }

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

            // Run after init & sync
            taskRunLater(1) {
                RuleListener
                CoroutineScope(Dispatchers.Default).launch {
                    ResourcePacks.entries // pre load all resource pack data
                }
            }
        }
    }

    override fun load() {
        INSTANCE = this@MChallenge
        consoleAudience = console
        debug = true

        CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(false).silentLogs(true))

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
        ConfigManager.addConfigurable(PositionCommand())
        ConfigManager.addConfigurable(BackpackCommand())

        // Load configuration
        prefix = cmp("MChallenge", cHighlight) + _prefixSeparator
        ConfigManager.addConfigurable(ChallengeManager)
        val settings = ConfigManager.settings
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
            Spectator.loadData()

            // Connect Bridge
            bridgeAPI = MUtilsBridge(MUtilsPlatform.PAPER, MUtilsModule.CHALLENGES, server.minecraftVersion, server.port, debug)
            val version = bridgeAPI.versionCheck(description.version.toIntOrNull() ?: 0, File("plugins/update"))
            //bridgeAPI.modrinthUpdate(File("plugins/update"))
            // TODO Prompt with click to update

            bridgeAPI.login({
                ChallengeManager.stopChallenges()
                challenges.forEach { (challenge, data) ->
                    if (challenge.filter.contains(ChallengeTags.FREE)) return@forEach
                    data.active = false
                }
                consoleAudience.sendMessage(prefix + cmp("Disabled all premium features. Please login with a valid account to continue", cError))
            }) {
                it.style
            }

            // Finish loading - starting setup
            isLoaded = true
        }
    }


    override fun shutdown() {
        CommandAPI.onDisable()
        ChallengeManager.shutDown()
        ConfigManager.save()
    }
}

val PluginManager by lazy { MChallenge.INSTANCE }
