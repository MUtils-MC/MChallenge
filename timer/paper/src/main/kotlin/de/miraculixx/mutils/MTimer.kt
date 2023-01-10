package de.miraculixx.mutils

import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.main.KSpigot
import de.miraculixx.mutils.command.TimerCommand
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.module.TimerManager
import de.miraculixx.mutils.utils.BukkitConfig
import de.miraculixx.mutils.utils.registerCommand
import java.io.File

class MTimer : KSpigot() {
    companion object {
        lateinit var INSTANCE: KSpigot
        val configFolder = File("plugins/MUtils/Timer")
        lateinit var localization: Localization
    }

    private lateinit var config: BukkitConfig

    override fun startup() {
        INSTANCE = this
        consoleAudience = console
        debug = true

        val versionSplit = server.minecraftVersion.split('.')
        majorVersion = versionSplit.getOrNull(1)?.toIntOrNull() ?: 0
        minorVersion = versionSplit.getOrNull(2)?.toIntOrNull() ?: 0

        if (!configFolder.exists()) configFolder.mkdirs()
        config = BukkitConfig(File("${configFolder.path}/settings.yml"), "settings")
        settings = config.getConfig()
        val languages = listOf("en_US").map { it to javaClass.getResourceAsStream("/language/$it.yml") }
        localization = Localization(File("${configFolder.path}/language"), settings.getString("language") ?: "en_US", languages)

        registerCommand("timer", TimerCommand(false))
        registerCommand("ptimer", TimerCommand(true))

        TimerManager.load(configFolder)
    }

    override fun shutdown() {
        config.save()
        TimerManager.save(configFolder)
    }
}

val PluginManager by lazy { MTimer.INSTANCE }