@file:Suppress("FunctionName", "ObjectPropertyName")

package de.miraculixx.mvanilla.messages

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.format.NamedTextColor

/**
 * Namespace for api related savings
 */
const val namespace = "de.miraculixx.api"

/**
 * Public console audience to use everywhere
 */
lateinit var consoleAudience: Audience

/**
 * Debug mode prints some nice crap into the console for debugging
 */
var debug = false

val _prefixSeparator = cmp(" >>", NamedTextColor.DARK_GRAY) + cmp(" ")

/**
 * Message prefix
 */
var prefix = cmp("MUtils", cHighlight) + _prefixSeparator

/**
 * Exact prefix
 */
var challengePrefix = cmp("MChallenge", cHighlight) + _prefixSeparator

/**
 * Timer Prefix
 */
var timerPrefix = cmp("MTimer", cHighlight) + _prefixSeparator

/**
 * 1.**19**.1
 */
var majorVersion: Int = 0

/**
 * 1.19.**1**
 */
var minorVersion: Int = 0

fun _reloadPrefix() {
    prefix = cmp("MUtils", cHighlight) + _prefixSeparator
    challengePrefix =  cmp("MChallenge", cHighlight) + _prefixSeparator
    timerPrefix = cmp("MTimer", cHighlight) + _prefixSeparator
}