package de.miraculixx.mutils.extensions

import de.miraculixx.mutils.messages.msgFalse
import de.miraculixx.mutils.messages.msgTrue

/**
 * Transform enum names into fancy strings.
 *
 * THE_END -> The End
 */
fun String.stringify(): String {
    return buildString {
        val words = split('_')
        words.forEach { word ->
            append(word[0].uppercase() + word.substring(1) + " ")
        }
    }.substring(0, length - 1)
}

fun Boolean.msg(): String {
    return if (this) msgTrue else msgFalse
}