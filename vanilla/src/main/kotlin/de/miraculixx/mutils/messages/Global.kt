package de.miraculixx.mutils.messages

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

/**
 * Message prefix
 */
val prefix = cmp("MUtils", cHighlight) + cmp(" >> ", NamedTextColor.DARK_GRAY)

val msgClick = cmp("Click", cHighlight) + cmp(" ≫ ")
val msgClickRight = cmp("Right-Click", cHighlight) + cmp(" ≫ ")
val msgShiftClickRight = cmp("Sneak-Right-Click", cHighlight) + cmp(" ≫ ")
val msgClickLeft = cmp("Left-Click", cHighlight) + cmp(" ≫ ")
val msgShiftClickLeft = cmp("Sneak-Left-Click", cHighlight) + cmp(" ≫ ")

/**
 * 1.**19**.1
 */
var majorVersion: Int = 0

/**
 * 1.19.**1**
 */
var minorVersion: Int = 0