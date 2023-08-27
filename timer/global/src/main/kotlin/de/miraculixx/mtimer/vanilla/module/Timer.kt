package de.miraculixx.mtimer.vanilla.module

import de.miraculixx.mtimer.vanilla.data.TimerDesignValue
import de.miraculixx.mtimer.vanilla.data.TimerPresets
import de.miraculixx.mvanilla.messages.miniMessages
import net.kyori.adventure.text.Component
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

abstract class Timer(
    designID: UUID? = null,
    val playerID: UUID? = null,
) {
    var animator = 0.0f
    var time = Duration.ZERO
    abstract var running: Boolean
    var countUp = true
    var visible = true
    var design = designID?.let { TimerManager.getDesign(it) } ?: TimerManager.getDesign(TimerPresets.CLASSIC.uuid) ?: TimerPresets.error
    var remove = false

    val tickLogics: MutableList<((Duration) -> Unit)> = mutableListOf()
    val stopLogics: MutableList<(() -> Unit)> = mutableListOf()
    val startLogics: MutableList<(() -> Unit)> = mutableListOf()

    fun addTickLogic(onTick: (Duration) -> Unit) {
        tickLogics.add(onTick)
    }

    fun addStopLogic(onStop: () -> Unit) {
        stopLogics.add(onStop)
    }

    fun addStartLogic(onStart: () -> Unit) {
        startLogics.add(onStart)
    }

    fun disableTimer() {
        remove = true
        visible = false
        disableListener()
    }

    abstract fun disableListener()

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
}