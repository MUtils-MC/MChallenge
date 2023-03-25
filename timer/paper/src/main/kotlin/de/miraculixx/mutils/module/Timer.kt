package de.miraculixx.mutils.module

import de.miraculixx.api.MChallengeAPI
import de.miraculixx.api.modules.challenges.ChallengeStatus
import de.miraculixx.kpaper.extensions.onlinePlayers
import de.miraculixx.kpaper.runnables.task
import de.miraculixx.mutils.MTimer
import de.miraculixx.mutils.data.TimerDesignValue
import de.miraculixx.mutils.data.TimerPresets
import de.miraculixx.mutils.messages.miniMessages
import de.miraculixx.mutils.messages.msg
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class Timer(
    private val isPersonal: Boolean,
    val player: OfflinePlayer? = null,
    designID: UUID? = null,
    activate: Boolean = true,
) {
    private var animator = 0.0f
    private var time = Duration.ZERO
    var running = false
        set(value) {
            field = value
            val api = MTimer.chAPI

            if (value) {
                listener?.activateTimer()
                if (rules.syncWithChallenge) {
                    if (api != null && api.status == ChallengeStatus.STOPPED) api.startChallenges()
                    else api?.resumeChallenges()
                }
            } else {
                listener?.deactivateTimer()
                if (rules.syncWithChallenge) api?.pauseChallenges()
            }
        }
    var countUp = true
    var visible = true
    var design = designID?.let { TimerManager.getDesign(it) } ?: TimerManager.getDesign(TimerPresets.CLASSIC.uuid) ?: TimerPresets.error
    private var remove = false
    private val listener = if (isPersonal) null else TimerListener()

    fun disableTimer() {
        remove = true
        visible = false
        listener?.disableAll()
    }

    fun setTime(duration: Duration) {
        time = duration
    }

    fun getTime(): Duration {
        return time
    }

    fun addTime(day: Int = 0, hour: Int = 0, min: Int = 0, sec: Int = 0): Boolean {
        val adder = day.days.plus(hour.hours).plus(min.minutes).plus(sec.seconds)
        return if (!(adder + time).isNegative()) {
            time += adder
            true
        } else false
    }

    fun buildSimple(): String {
        return buildString {
            time.toComponents { days, hours, minutes, seconds, _ ->
                if (days > 0) append("$days ")
                if (time.inWholeHours > 0) append("${replaceNumber(true, hours)}:")
                append("${replaceNumber(true, minutes)}:${replaceNumber(true, seconds)}")
            }
        }
    }

    fun buildFormatted(running: Boolean): Component {
        val formatter = if (running) design.running else design.idle
        var syntax = formatter.syntax
        syntax = syntax.replace("<prefix>", formatter.prefix)
        syntax = syntax.replace("<suffix>", formatter.suffix)
        time.toComponents { days, hours, minutes, seconds, millis ->
            syntax = replaceDigit(syntax, "<d>", formatter.days, days.toInt())
            syntax = replaceDigit(syntax, "<h>", formatter.hours, hours)
            syntax = replaceDigit(syntax, "<m>", formatter.minutes, minutes)
            syntax = replaceDigit(syntax, "<s>", formatter.seconds, seconds)
            syntax = replaceDigit(syntax, "<ms>", formatter.millis, millis / 1000000)
        }
        syntax = syntax.replace("<x>", animator.toString())
        return miniMessages.deserialize(syntax)
    }

    private fun replaceDigit(base: String, key: String, data: TimerDesignValue, value: Int): String {
        val final = if (data.visibleOnNull || value > 0)
            "${data.prefix}${replaceNumber(data.forcedTwoDigits, value)}${data.suffix}"
        else ""
        return base.replace(key, final)
    }

    private fun replaceNumber(forcedTwo: Boolean, value: Int): String {
        return if (forcedTwo && value <= 9) "0$value" else "$value"
    }

    private fun run() {
        task(false, 0, 1) {
            if (remove) it.cancel()
            if (!visible) return@task
            if (player?.isOnline == false) return@task
            val target = if (isPersonal) listOf(player?.player) else {
                if (running) onlinePlayers else onlinePlayers.filter { player ->
                    val p = TimerManager.getPersonalTimer(player.uniqueId)
                    if (p == null) true else !(p.visible)
                }
            }

            animator += if (running) design.running.animationSpeed else design.idle.animationSpeed
            if (animator > 1.0f) animator -= 2.0f
            else if (animator < -1.0f) animator += 2.0f

            val globalTimer = if (isPersonal) TimerManager.getGlobalTimer() else this
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
                    p?.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.1f)
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