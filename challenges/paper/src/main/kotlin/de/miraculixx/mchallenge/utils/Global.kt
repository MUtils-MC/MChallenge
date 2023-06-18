package de.miraculixx.mchallenge.utils

import de.miraculixx.mchallenge.global.Challenges
import de.miraculixx.mvanilla.messages.cHighlight
import de.miraculixx.mvanilla.messages.cMark
import de.miraculixx.mvanilla.messages.cmp
import de.miraculixx.mvanilla.messages.plus
import net.kyori.adventure.text.Component

var cotm: Challenges = Challenges.FLY

fun getRPPrompt(action: String, input: String): Component {
    return cmp("MUtils Challenges", cHighlight, bold = true) +
            cmp("\n\nClick on accept to $action ") + cmp(input, cMark) + cmp(".") +
            cmp("\nOtherwise you will be disconnected from this server")
}
