package de.miraculixx.mtimer

import de.miraculixx.challenge.api.MChallengeAPI
import de.miraculixx.mtimer.commands.TimerCommand
import de.miraculixx.mtimer.events.CustomPlayer
import de.miraculixx.mtimer.module.load
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mutils.gui.utils.adventure
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.server.MinecraftServer
import net.silkmc.silk.core.event.Entity
import net.silkmc.silk.core.event.Events
import net.silkmc.silk.core.event.Player
import net.silkmc.silk.core.event.Server
import java.io.File

lateinit var server: MinecraftServer
lateinit var challengeAPI: MChallengeAPI

fun init() {
    var configFolder: String
    TimerCommand

    Events.Server.postStart.listen { event ->
        server = event.server
        adventure = FabricServerAudiences.of(server)
        consoleAudience = adventure.console()
        cmp("Test")
        consoleAudience.sendMessage(cmp("DIES IST EIN ADVENTURE TEST"))
        println("- PrintLN to console")

        //TODO -> Implement all data loading
        configFolder = "${server.serverDirectory.path}/config/MUtils/Timer"
        val languages = listOf("en_US", "de_DE", "es_ES").map { it to Unit::class.java.getResourceAsStream("/language/$it.yml") }
        Localization(File("$configFolder/language"), "en_US", languages, timerPrefix)

        TimerManager.load(File(configFolder))
    }

    Events.Server.preStop.listen {
        consoleAudience.sendMessage(prefix + cmp("Successfully saved all data! Good Bye :)"))
    }

    Events.CustomPlayer.preDrop.listen { event ->
        println("DropEvent -> ${event.player.scoreboardName} ${event.item.item.getName(event.item).string}")
    }

    Events.CustomPlayer.onFinalDamage.listen { event ->
        println("DamageEvent -> ${event.player.scoreboardName} ${event.damageSource.entity?.name?.string} ${event.damage}")
    }

    Events.CustomPlayer.preBlockBreak.listen { event ->
        println("BlockBreak -> ${event.player.scoreboardName} ${event.level.getBlockState(event.blockPos).block.name.string}")
    }
}