package de.miraculixx.mchallenge.utils

import de.miraculixx.mcommons.text.cHighlight
import de.miraculixx.mcommons.text.cMark
import de.miraculixx.mcommons.text.cmp
import de.miraculixx.mcommons.text.plus
import net.kyori.adventure.text.Component

fun getRPPrompt(action: String, input: String): Component {
    return cmp("MUtils Challenges", cHighlight, bold = true) +
            cmp("\n\nClick on accept to $action ") + cmp(input, cMark) + cmp(".") +
            cmp("\nOtherwise you will be disconnected from this server")
}
