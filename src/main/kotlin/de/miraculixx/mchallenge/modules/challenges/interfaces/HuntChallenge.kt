package de.miraculixx.mchallenge.modules.challenges.interfaces

import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.broadcast
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.mchallenge.MChallenge
import de.miraculixx.mchallenge.modules.ChallengeManager
import de.miraculixx.mchallenge.utils.Command
import de.miraculixx.mchallenge.utils.command
import de.miraculixx.mchallenge.utils.config.loadConfig
import de.miraculixx.mchallenge.utils.config.saveConfig
import de.miraculixx.mchallenge.utils.serializer.Serializer
import de.miraculixx.mcommons.serializer.miniMessage
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.prefix
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.literalArgument
import kotlinx.serialization.Serializable
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File

abstract class HuntChallenge<T>(name: String, val key: String) : CommandChallenge {
    abstract val remainingEntries: MutableList<T>
    abstract val allEntries: List<T>
    abstract val typeName: String
    abstract var currentTarget: T?
    abstract val maxEntries: Int
    abstract val serializer: Serializer<T>

    private val dataFile = File("${MChallenge.configFolder.path}/data/$key.json")
    private val bar = BossBar.bossBar(cmp("Waiting for server..."), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)

    override val command: Command = command(name) {
        literalArgument("skip") {
            anyExecutor { sender, _ ->
                nextEntry(sender.name, sender)
            }
        }
        literalArgument("reset") {
            anyExecutor { _, _ ->
                remainingEntries.clear()
                remainingEntries.addAll(allEntries)
                calcBar()
            }
        }
    }

    abstract fun getTranslationKey(): String?

    fun nextEntry(playerName: String, audience: Audience) {
        broadcast(prefix, "event.$key.collect", listOf(playerName, getTranslationKey() ?: ""))
        audience.playSound(Sound.sound(Key.key("entity.chicken.egg"), Sound.Source.MASTER, 1f, 1.2f))
        currentTarget = if (remainingEntries.isEmpty()) {
            broadcast(prefix, "event.$key.success")
            ChallengeManager.stopChallenges()
            null
        } else remainingEntries.random()
        remainingEntries.remove(currentTarget)
        calcBar()
    }

    private fun calcBar() {
        val collectedAmount = maxEntries - remainingEntries.size
        val target = getTranslationKey()?.let { "<blue><b>$it<blue><b>" } ?: "<green><b>Finished</b></green>"
        bar.name(miniMessage.deserialize("<gray>$typeName:</gray> $target <dark_gray>(<gray><green>$collectedAmount</green>/<red>$maxEntries</red></gray>)</dark_gray>"))
    }


    //
    // Common Events
    //
    private val onJoin = listen<PlayerJoinEvent> {
        it.player.showBossBar(bar)
    }


    //
    // Challenge Lifecycle
    //
    fun startHunt() {
        registerCommand()
        val preData = dataFile.loadConfig(HuntData())
        val preTarget = preData.currentTarget
        currentTarget = if (preTarget == null) {
            remainingEntries.addAll(allEntries)
            remainingEntries.random()
        } else {
            remainingEntries.addAll(preData.remainingEntries.map { serializer.toObject(it) })
            serializer.toObject(preTarget)
        }

        remainingEntries.remove(currentTarget)
        calcBar()
        onlinePlayers.forEach { it.showBossBar(bar) }
        onJoin.register()
    }

    fun stopHunt() {
        unregisterCommand()
        dataFile.saveConfig(HuntData(currentTarget?.let { serializer.toString(it) }, remainingEntries.map { serializer.toString(it) }))
        onlinePlayers.forEach { it.hideBossBar(bar) }
        onJoin.unregister()
    }


    //
    // Data Holder
    //
    @Serializable
    data class HuntData(
        val currentTarget: String? = null,
        val remainingEntries: List<String> = emptyList()
    )
}