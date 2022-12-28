package de.miraculixx.mutils.extensions

import java.math.RoundingMode

fun Float.round(digits: Int): Float {
    return toBigDecimal().setScale(digits, RoundingMode.HALF_UP).toFloat()
}

fun Double.round(digits: Int): Double {
    return toBigDecimal().setScale(digits, RoundingMode.HALF_UP).toDouble()
}