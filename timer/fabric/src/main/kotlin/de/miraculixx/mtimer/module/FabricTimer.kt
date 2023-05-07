package de.miraculixx.mtimer.module

import de.miraculixx.challenge.api.MChallengeAPI
import de.miraculixx.challenge.api.modules.challenges.ChallengeStatus
import de.miraculixx.mtimer.vanilla.module.Timer
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mtimer.vanilla.module.rules
import de.miraculixx.mvanilla.messages.*
import kotlinx.coroutines.cancel
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.title.Title
import net.minecraft.server.players.PlayerList
import net.silkmc.silk.core.task.infiniteMcCoroutineTask
import java.util.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class FabricTimer(
    private val isPersonal: Boolean,
    playerID: UUID? = null,
    designID: UUID? = null,
    activate: Boolean = true,
    private val playerList: PlayerList
): Timer(designID, playerID) {
//    private val listener
    override var running = false
        set(value) {
            field = value
            val api: MChallengeAPI? = null

            if (value) {
//                listener?.activateTimer()
                startLogics.forEach { it.invoke() }
                if (rules.syncWithChallenge) {
                    if (api != null) {
                        when (api.getChallengeStatus()) {
                            ChallengeStatus.STOPPED -> api.startChallenges()
                            ChallengeStatus.PAUSED -> api.resumeChallenges()
                            ChallengeStatus.RUNNING -> if (debug) consoleAudience.sendMessage(prefix + cmp("Challenges already running!", cError))
                        }
                    } else consoleAudience.sendMessage(prefix + cmp("Failed to sync with MChallenge!", cError))
                }
            } else {
//                listener?.deactivateTimer()
                stopLogics.forEach { it.invoke() }
                if (rules.syncWithChallenge) {
                    if (api != null) {
                        api.pauseChallenges()
                    } else consoleAudience.sendMessage(prefix + cmp("Failed to sync with MChallenge!", cError))
                }
            }
        }

    override fun disableListener() {

    }

    private fun run() {
        infiniteMcCoroutineTask(false) {
            if (remove) cancel()
            if (!visible) return@infiniteMcCoroutineTask
            val player = playerID?.let { playerList.getPlayer(it) } ?: return@infiniteMcCoroutineTask
            tickLogics.forEach { tick -> tick.invoke(time) }

            val target = if (isPersonal) listOf(player) else {
                if (running) playerList.players else playerList.players.filter { sp ->
                    val p = TimerManager.getPersonalTimer(sp.uuid)
                    if (p == null) true else !(p.visible)
                }
            }

            animator += if (running) design.running.animationSpeed else design.idle.animationSpeed
            if (animator > 1.0f) animator -= 2.0f
            else if (animator < -1.0f) animator += 2.0f

            val globalTimer = if (isPersonal) TimerManager.globalTimer else this@FabricTimer
            if (!isPersonal || (!globalTimer.visible || !globalTimer.running)) {
                val component = buildFormatted(running)
                target.forEach { t -> t.sendActionBar(component) }
            }

            if (!running) return@infiniteMcCoroutineTask
            if (time < 0.seconds) {
                running = false
                val title = Title.title(
                    msg("event.timeout.1"), msg("event.timeout.2"),
                    Title.Times.times(java.time.Duration.ofMillis(300), java.time.Duration.ofMillis(5000), java.time.Duration.ofMillis(1000))
                ) // 0,3s 5s 1s
                target.forEach { p ->
                    p.playSound(Sound.sound(Key.key("entity.ender_dragon.growl"), Sound.Source.MASTER, 1f, 1.1f))
                    p.showTitle(title)
                }
                return@infiniteMcCoroutineTask
            }

            time += if (countUp) 50.milliseconds else (-50).milliseconds
        }
    }

    init {
        if (activate) run()
    }
}