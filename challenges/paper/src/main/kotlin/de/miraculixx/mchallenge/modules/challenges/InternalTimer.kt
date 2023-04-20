package de.miraculixx.mchallenge.modules.challenges

import de.miraculixx.kpaper.runnables.KSpigotRunnable
import de.miraculixx.kpaper.runnables.task
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class InternalTimer(
    startTime: Duration,
    private val onNull: (KSpigotRunnable) -> Unit,
    private val onUpdate: (KSpigotRunnable, Duration) -> Unit,
) {
    var time = startTime
    var running = true
    var stopped = false

    val scheduler = task(false, 0, 20) {
        if (stopped) it.cancel()
        if (!running) return@task
        if (time == 0.seconds) {
            time -= 1.seconds
            onNull.invoke(it)
            it.cancel()
        } else if (time > 0.seconds) {
            time -= 1.seconds
            onUpdate.invoke(it, time)
        }
    }
}

fun Duration.getFormatted(): String {
    return buildString {
        toComponents { days, hours, minutes, seconds, _ ->
            if (days > 0) append("$days ")
            if (hours > 0) append("${hours.convertDigit()}:")
            append("${minutes.convertDigit()}:${seconds.convertDigit()}")
        }
    }
}

private fun Int.convertDigit(): String {
    return if (this > 9) "$this" else "0$this"
}