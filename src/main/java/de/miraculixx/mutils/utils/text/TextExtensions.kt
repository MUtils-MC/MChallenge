package de.miraculixx.mutils.utils.text

fun String.fancy(): String {
    return buildString {
        this@fancy.lowercase().forEachIndexed { index, c ->
            when {
                index == 0 -> append(c.uppercase())
                c == '_' -> append(' ')
                this@fancy[(index - 1).coerceIn(this@fancy.indices)] == '_' -> append(c.uppercase())

                else -> append(c)
            }
        }
    }
}