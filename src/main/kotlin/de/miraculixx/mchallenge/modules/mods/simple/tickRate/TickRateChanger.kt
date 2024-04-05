@file:Suppress("UnstableApiUsage")

package de.miraculixx.mchallenge.modules.mods.simple.tickRate

import de.miraculixx.challenge.api.modules.challenges.Challenge
import de.miraculixx.kpaper.event.listen
import de.miraculixx.kpaper.event.register
import de.miraculixx.kpaper.event.unregister
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.extensions.server
import de.miraculixx.mchallenge.modules.packs.ResourcePackManager
import de.miraculixx.mchallenge.modules.packs.ResourcePacks
import de.miraculixx.mchallenge.utils.getDominantLocale
import de.miraculixx.mcommons.text.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.event.player.PlayerJoinEvent
import kotlin.time.Duration.Companion.milliseconds

class TickRateChanger : Challenge {
    private val msgGameSpeed = getDominantLocale().msgString("event.tickRate.gameSpeed")
    private val bossBar = BossBar.bossBar(cmp(msgGameSpeed, cHighlight) + cmp(" >> ") + cmp("${getTickPercentage()}%", cMark, true), 1f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)
    private val minTicksToChange = 20 * 8
    private val maxTicksToChange = 20 * 30
    private val minTick = 3
    private val maxTick = 80
    private var paused = false

    override fun register() {
        onJoin.register()
        paused = false
    }

    override fun unregister() {
        onJoin.unregister()
        paused = true
    }

    override fun start(): Boolean {
        ResourcePackManager.sendToAll(ResourcePacks.CLEAR_BOSSBAR)
        onlinePlayers.forEach { p -> p.showBossBar(bossBar) }
        return true
    }

    override fun stop() {
        server.serverTickManager.tickRate = 20f
        ResourcePackManager.removeFromAll(ResourcePacks.CLEAR_BOSSBAR)
        onlinePlayers.forEach { p -> p.hideBossBar(bossBar) }
        tickTask.cancel()
    }

    private val onJoin = listen<PlayerJoinEvent>(register = false) {
        ResourcePackManager.sendTo(ResourcePacks.CLEAR_BOSSBAR, it.player)
        it.player.showBossBar(bossBar)
    }


    private var timeToNextChange = (minTicksToChange..maxTicksToChange).random()
    private val tickTask = CoroutineScope(Dispatchers.Default).launch {
        while (true) {
            if (paused) continue
            if (timeToNextChange <= 0) {
                scaleTickRate((minTick..maxTick).random())
                timeToNextChange = (minTicksToChange..maxTicksToChange).random()
            }

            timeToNextChange--
            delay(50.milliseconds)
        }
    }

    private fun scaleTickRate(nextTickTarget: Int) {
        val currentTickRate = server.serverTickManager.tickRate
        val tickChangeRate = (nextTickTarget - currentTickRate) / 20f
        val up = tickChangeRate > 0
        val suffix = if (up) cmp("↑", cSuccess, true) else cmp("↓", cSuccess, true)
        if (up) Audience.audience(onlinePlayers).playSound(Sound.sound(Key.key("block.beacon.power_select"), Sound.Source.MASTER, 0.7f, 1f))
        else Audience.audience(onlinePlayers).playSound(Sound.sound(Key.key("block.beacon.deactivate"), Sound.Source.MASTER, 0.7f, 1f))
        var changedTicks = 0
        CoroutineScope(Dispatchers.Default).launch {
            var scaling = true
            while (scaling) {
                if (paused) continue
                if (changedTicks >= 20) {
                    scaling = false
                } else {
                    server.serverTickManager.tickRate += tickChangeRate
                    bossBar.progress(server.serverTickManager.tickRate / maxTick)
                    bossBar.name(cmp(msgGameSpeed, cHighlight) + cmp(" >> ") + cmp("${getTickPercentage()}%", cMark, true) + suffix)
                    changedTicks++
                }
                delay(50.milliseconds)
            }
            delay(500.milliseconds)

            bossBar.name(cmp(msgGameSpeed, cHighlight) + cmp(" >> ") + cmp("${getTickPercentage()}%", cMark, true))
        }
    }

    private fun getTickPercentage(): Int = ((server.serverTickManager.tickRate / 20) * 100).toInt()
}