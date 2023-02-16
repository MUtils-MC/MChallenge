package de.miraculixx.mutils.extensions

import de.miraculixx.mutils.messages.msgFalse
import de.miraculixx.mutils.messages.msgTrue

fun Boolean.msg(): String {
    return if (this) msgTrue else msgFalse
}