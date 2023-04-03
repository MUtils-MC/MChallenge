package de.miraculixx.mvanilla.extensions

import de.miraculixx.mvanilla.messages.msgFalse
import de.miraculixx.mvanilla.messages.msgTrue

fun Boolean.msg(): String {
    return if (this) msgTrue else msgFalse
}