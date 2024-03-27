package de.miraculixx.mvanilla.messages

import net.kyori.adventure.text.Component

// Common messages
private val separator = Component.text("-")
val msgClick = cmp(msgString("common.click"), cHighlight) + cmp(" ≫ ")
val msgClickRight = cmp(msgString("common.right"), cHighlight).append(separator) + msgClick
val msgShiftClickRight = cmp(msgString("common.sneak"), cHighlight).append(separator) + msgClickRight
val msgClickLeft = cmp(msgString("common.left"), cHighlight).append(separator) + msgClick
val msgShiftClickLeft = cmp(msgString("common.sneak"), cHighlight).append(separator) + msgClickLeft
val msgShiftClick = cmp(msgString("common.sneak"), cHighlight).append(separator) + msgClick
val msgTrue = msgString("common.boolTrue")
val msgFalse = msgString("common.boolFalse")
val msgNone = msgString("common.none")