package de.miraculixx.mutils.utils

import de.miraculixx.mutils.enums.Challenges
import de.miraculixx.mutils.messages.cHighlight
import de.miraculixx.mutils.messages.cMark
import de.miraculixx.mutils.messages.cmp
import de.miraculixx.mutils.messages.plus
import net.kyori.adventure.text.Component

lateinit var cotm: Challenges

fun getRPPrompt(action: String, input: String): Component {
    return cmp("MUtils Challenges", cHighlight, bold = true) +
            cmp("\n\nClick on accept to $action ") + cmp(input, cMark) + cmp(".") +
            cmp("\nOtherwise you will be disconnected from this server")
}
