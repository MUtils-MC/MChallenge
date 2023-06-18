package de.miraculixx.mtimer.module

import de.miraculixx.challenge.api.modules.challenges.ChallengeStatus
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mtimer.MTimer
import de.miraculixx.mtimer.vanilla.module.Timer
import de.miraculixx.mtimer.vanilla.module.TimerManager
import de.miraculixx.mtimer.vanilla.module.rules
import de.miraculixx.mvanilla.messages.*
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import java.util.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class PaperTimer(
    private val isPersonal: Boolean,
    playerID: UUID? = null,
    designID: UUID? = null,
    activate: Boolean = true,
): Timer(designID) {
    private val player = playerID?.let { Bukkit.getOfflinePlayer(it) }
    private val listener = if (isPersonal) null else TimerListener()
    override var running = false
        set(value) {
            field = value
            val api = MTimer.chAPI

            if (value) {
                listener?.activateTimer()
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
                listener?.deactivateTimer()
                stopLogics.forEach { it.invoke() }
                if (rules.syncWithChallenge) {
                    if (api != null) {
                        api.pauseChallenges()
                    } else consoleAudience.sendMessage(prefix + cmp("Failed to sync with MChallenge!", cError))
                }
            }
        }

    override fun disableListener() {
        listener?.disableAll()
    }

    private fun run() {
        task(false, 0, 1) {
            if (remove) it.cancel()
            if (!visible) return@task
            if (player?.isOnline == false) return@task
            tickLogics.forEach { tick -> tick.invoke(time) }

            val target = if (isPersonal) listOf(player?.player) else {
                if (running) onlinePlayers else onlinePlayers.filter { player ->
                    val p = TimerManager.getPersonalTimer(player.uniqueId)
                    if (p == null) true else !(p.visible)
                }
            }

            animator += if (running) design.running.animationSpeed else design.idle.animationSpeed
            if (animator > 1.0f) animator -= 2.0f
            else if (animator < -1.0f) animator += 2.0f

            val globalTimer = if (isPersonal) TimerManager.globalTimer else this
            if (!isPersonal || (!globalTimer.visible || !globalTimer.running)) {
                val component = buildFormatted(running)
                target.forEach { t -> t?.sendActionBar(component) }
            }

            if (!running) return@task
            if (time < 0.seconds) {
                running = false
                val title = Title.title(
                    msg("event.timeout.1"), msg("event.timeout.2"),
                    Title.Times.times(java.time.Duration.ofMillis(300), java.time.Duration.ofMillis(5000), java.time.Duration.ofMillis(1000))
                ) // 0,3s 5s 1s
                target.forEach { p ->
                    p?.playSound(p, org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.1f)
                    p?.showTitle(title)
                }
                return@task
            }

            time += if (countUp) 50.milliseconds else (-50).milliseconds
        }
    }

    init {
        if (activate) run()
    }
}