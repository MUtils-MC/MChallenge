@file:Suppress("SpellCheckingInspection")

package de.miraculixx.mutils.system.boot

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.commands.*
import de.miraculixx.mutils.commands.tools.GlobalOverride
import de.miraculixx.mutils.commands.utils.*
import de.miraculixx.mutils.modules.ModuleManager
import de.miraculixx.mutils.modules.challenge.ChallengeCommand
import de.miraculixx.mutils.modules.challenge.mods.limitedSkills.LimitSkillsCommand
import de.miraculixx.mutils.modules.creator.tools.CreatorCommand
import de.miraculixx.mutils.modules.gui.GUIListener
import de.miraculixx.mutils.modules.spectator.SpecCommand
import de.miraculixx.mutils.modules.spectator.Spectator
import de.miraculixx.mutils.modules.speedrun.SpeedrunCommand
import de.miraculixx.mutils.modules.speedrun.SpeedrunListener
import de.miraculixx.mutils.modules.timer.TimerCommand
import de.miraculixx.mutils.modules.utils.back.BackCommand
import de.miraculixx.mutils.modules.utils.backpack.BackPackCommand
import de.miraculixx.mutils.modules.utils.reminder.ReminderCommand
import de.miraculixx.mutils.modules.utils.tools.Connection
import de.miraculixx.mutils.modules.worldManager.WorldCommand
import de.miraculixx.mutils.modules.worldManager.WorldListener
import de.miraculixx.mutils.system.config.Config
import de.miraculixx.mutils.system.config.ConfigCommand
import de.miraculixx.mutils.system.config.ConfigManager
import de.miraculixx.mutils.system.config.Configs
import de.miraculixx.mutils.utils.*
import de.miraculixx.mutils.utils.text.consoleMessage
import de.miraculixx.mutils.utils.text.consoleWarn
import kotlinx.coroutines.runBlocking
import net.axay.kspigot.extensions.server
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class StartUp(private val plugin: JavaPlugin) {

    init {
        if (connect()) {
            startUp()
        }
    }

    private fun connect(): Boolean {
        val versions: Pair<Boolean, Boolean>
        val vPlugin = plugin.description.version
        val data: API.Version

        // Grabbing Data from API
        runBlocking {
            data = API.getVersion()
            val vNumber = vPlugin.replace(".", "").toInt()
            versions = API.versionCheck(vNumber, data)
            val verify = ConfigManager.getLicenceData()
            val serverVersion = server.version.removePrefix("git-").replace(" (MC: ", "_").removeSuffix(")")
            premium = API.login(verify.first, IP, verify.second, if (serverVersion.length > 32) server.minecraftVersion else serverVersion, vPlugin)
            challengeOfTheMonth = API.challengeOfMonth()

            // Download Data
            val conf = ConfigManager.getConfig(Configs.SETTINGS)
            val s = File.separator
            val path = Main.INSTANCE.dataFolder.path + s
            if (conf.getBoolean("Sync Load.Modules")) {
                //API.download("private/$ID-${verify.first}/modules.yml").copyTo(Path(path + "modules${s}modules.yml").toFile(), true) TODO
                ConfigManager.reload(Configs.MODULES)
            }
        }

        consoleMessage("$prefix Debug Mode >> §coff")
        consoleMessage("$prefix API Version >> §e$vPlugin")
        if (!versions.second) {
            consoleWarn(
                "$prefix New Version detected! You are running an outdated Version, please update!",
                "$prefix Your current Version is no longer supported due to security or API updates.",
                "$prefix Your Version >> §n$vPlugin§e - Latest Version >> §a§n${data.latest}",
                "$prefix Download: https://mutils.de/m/download"
            )
            consoleMessage("§cMUtils disabled! Features may still show up but can throw errors")
            if (data.last != 0) API.updatePlugin()
            return false
        }
        if (!versions.first) {
            consoleWarn(
                "$prefix New Version detected! You are running an outdated Version, please update!",
                "$prefix Currently last supported version is ${data.last}",
                "$prefix Your Version >> §n$vPlugin§e - Latest Version >> §a§n${data.latest}",
                "$prefix Download: https://mutils.de/m/download"
            )
            if (data.last != 0) API.updatePlugin()
            API.updatePlugin()
        }
        if (premium)
            consoleMessage("$prefix §aSuccessfully logged in! Have fun with MUtils")
        else {
            consoleMessage("$prefix §cCannot verify your Key! MUtils is a Premium Plugin that can only be used with a valid Key!")
            consoleMessage("$prefix §cYou can buy a Key at https://mutils.de/m/shop")
            consoleMessage("$prefix Believe this is an error? Already have a valid Key? Get help at https://mutils.de/dc")
        }

        return true
    }

    private fun startUp() {
        // Preload indirect config files to show up directly
        Config("language/custom")

        // Global Listener Registration
        ModuleManager
        GUIListener
        GlobalOverride
        Connection
        WorldListener

        if (premium) {
            // Premium Listener Registration
            Spectator
            SpeedrunListener
        } else DemoAlert()

        enableCommands()
    }

    private fun enableCommands() {
        if (premium) {
            val spec = SpecCommand()
            plugin.getCommand("spec")?.setExecutor(spec)
            plugin.getCommand("spec")?.tabCompleter = spec
            val position = PositionCommand()
            plugin.getCommand("position")?.setExecutor(position)
            plugin.getCommand("position")?.tabCompleter = position
            val heal = HealCommand()
            plugin.getCommand("heal")?.setExecutor(heal)
            plugin.getCommand("heal")?.tabCompleter = heal
            plugin.getCommand("back")?.setExecutor(BackCommand())
            val texturePack = TexturePack()
            plugin.getCommand("texturepack")?.setExecutor(texturePack)
            plugin.getCommand("texturepack")?.tabCompleter = texturePack
            val invsee = InvSee()
            plugin.getCommand("invsee")?.setExecutor(invsee)
            plugin.getCommand("invsee")?.tabCompleter = invsee
            val backpack = BackPackCommand()
            plugin.getCommand("backpack")?.setExecutor(backpack)
            plugin.getCommand("backpack")?.tabCompleter = backpack
            plugin.getCommand("speedrun")?.setExecutor(SpeedrunCommand())
            plugin.getCommand("speedrun")?.tabCompleter = SpeedrunCommand()
            val world = WorldCommand()
            plugin.getCommand("world")?.setExecutor(world)
            plugin.getCommand("world")?.tabCompleter = world
            val reminder = ReminderCommand()
            plugin.getCommand("reminder")?.setExecutor(reminder)
            plugin.getCommand("reminder")?.tabCompleter = reminder
        } else {
            val noLicenceClass = NoLicenceCommand()
            listOf("spec", "position", "heal", "back", "texturepack", "invsee", "backpack", "speedrun", "world", "reminder")
                .forEach {
                    plugin.getCommand(it)?.setExecutor(noLicenceClass)
                    plugin.getCommand(it)?.tabCompleter = noLicenceClass
                }
        }
        val chdebug = DebugChallenge()
        plugin.getCommand("chdebug")?.setExecutor(chdebug)
        plugin.getCommand("chdebug")?.tabCompleter = chdebug
        val challenge = ChallengeCommand()
        plugin.getCommand("challenge")?.setExecutor(challenge)
        plugin.getCommand("challenge")?.tabCompleter = challenge
        val gamemode = GamemodeCommand()
        plugin.getCommand("gamemode")?.setExecutor(gamemode)
        plugin.getCommand("gamemode")?.tabCompleter = gamemode
        val reset = ResetCommand()
        plugin.getCommand("reset")?.setExecutor(reset)
        plugin.getCommand("reset")?.tabCompleter = reset
        val timer = TimerCommand()
        plugin.getCommand("timer")?.setExecutor(timer)
        plugin.getCommand("timer")?.tabCompleter = timer
        val ls = LimitSkillsCommand()
        plugin.getCommand("limitedskills")?.setExecutor(ls)
        plugin.getCommand("limitedskills")?.tabCompleter = ls
        val conf = ConfigCommand()
        plugin.getCommand("config")?.setExecutor(conf)
        plugin.getCommand("config")?.tabCompleter = conf
        val settings = SettingsCommand()
        plugin.getCommand("settings")?.setExecutor(settings)
        plugin.getCommand("settings")?.tabCompleter = settings
        val language = LanguageCommand()
        plugin.getCommand("language")?.setExecutor(language)
        plugin.getCommand("language")?.tabCompleter = language
        val verify = VerifyCommand()
        plugin.getCommand("verify")?.setExecutor(verify)
        plugin.getCommand("verify")?.tabCompleter = verify
        plugin.getCommand("creator")?.setExecutor(CreatorCommand())
        plugin.getCommand("debuginv")?.setExecutor(TestInventory())
    }
}