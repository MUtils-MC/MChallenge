package de.miraculixx.mutils

import de.miraculixx.kpaper.extensions.console
import de.miraculixx.kpaper.main.KSpigot
import de.miraculixx.mutils.api.MUtilsAPI
import de.miraculixx.mutils.enums.Challenges
import de.miraculixx.mutils.extensions.enumOf
import de.miraculixx.mutils.messages.*
import de.miraculixx.mutils.utils.BukkitConfig
import de.miraculixx.mutils.utils.cotm
import de.miraculixx.mutils.utils.settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MChallenge : KSpigot() {
    companion object {
        lateinit var INSTANCE: KSpigot
        val configFolder = File("plugins/MUtils/Challenges")
        lateinit var localization: Localization
        lateinit var api: MUtilsAPI
    }

    private lateinit var config: BukkitConfig

    override fun load() {
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

        CoroutineScope(Dispatchers.Default).launch {
            api = MUtilsAPI("challenges", description.version.toInt(), configFolder, "${server.ip}:${server.port}")
            cotm = enumOf<Challenges>(api.getCOTM()) ?: Challenges.FLY
        }
    }
}

val PluginManager by lazy { MChallenge.INSTANCE }
