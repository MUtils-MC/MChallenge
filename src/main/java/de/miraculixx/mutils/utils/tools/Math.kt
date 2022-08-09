package de.miraculixx.mutils.utils.tools

import kotlin.math.round
import kotlin.math.roundToInt

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

fun Double.toTPS(): String {
    return when (val input = if (this > 20.0) 20.0 else this) {
        in 18.5..20.0 -> "§a$input"
        in 16.0..18.4 -> "§e$input"
        else -> "§c$input"
    }
}

fun Double.toLag(): String {
    val input = if (this > 20.0) 20.0 else this
    val percentage = ((1.0 - input / 20.0) * 100.0).roundToInt().toDouble()
    return when (input) {
        in 18.5..20.0 -> "§a$percentage%"
        in 16.0..18.4 -> "§e$percentage%"
        else -> "§c$percentage%"
    }
}

fun Long.toMemPercent(max: Long): Int {
    return ((this.toDouble() / max.toDouble()) * 100).toInt()
}

fun Long.toMemory(max: Long): String {
    return when ((this / max).toDouble()) {
        in 0.0..0.49 -> "§a$this/$max"
        in 0.5..0.8 -> "§e$this/$max"
        else -> "§c$this/$max"
    }
}
