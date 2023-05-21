package de.miraculixx.mtimer

import de.miraculixx.challenge.api.MChallengeAPI
import de.miraculixx.mtimer.commands.TimerCommand
import de.miraculixx.mtimer.module.TimerListener
import de.miraculixx.mtimer.module.load
import de.miraculixx.mtimer.module.pauseGlobalTimer
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mutils.gui.utils.adventure
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.server.MinecraftServer
import net.silkmc.silk.core.event.Events
import net.silkmc.silk.core.event.Server
import java.io.File

lateinit var server: MinecraftServer
lateinit var challengeAPI: MChallengeAPI
lateinit var configFolder: String

fun init() {
    TimerCommand

    Events.Server.postStart.listen { event ->
        debug = true
        server = event.server
        adventure = FabricServerAudiences.of(server)
        consoleAudience = adventure.console()

        val version = server.serverVersion.split('.')
        majorVersion = version.getOrNull(1)?.toIntOrNull() ?: 0
        minorVersion = version.getOrNull(2)?.toIntOrNull() ?: 0

        configFolder = "${server.serverDirectory.path}/config/MUtils/Timer"
        val languages = listOf("en_US", "de_DE", "es_ES").map { it to Unit::class.java.getResourceAsStream("/language/$it.yml") }
        Localization(File("$configFolder/language"), "en_US", languages, timerPrefix)

        TimerManager.load(File(configFolder))
        pauseGlobalTimer()
        TimerListener
    }

    Events.Server.preStop.listen {
        consoleAudience.sendMessage(prefix + cmp("Successfully saved all data! Good Bye :)"))
    }

}