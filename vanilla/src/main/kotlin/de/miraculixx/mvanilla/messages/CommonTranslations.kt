package de.miraculixx.mvanilla.messages

import net.kyori.adventure.text.Component

// Common messages
private val separator = Component.text("-")
val msgClick = cmp(msgString("common.click"), cHighlight) + cmp(" ≫ ")
val msgClickRight = cmp(msgString("common.right"), cHighlight).append(separator) + msgClick
val msgShiftClickRight = cmp(msgString("common.sneak"), cHighlight).append(separator) + msgClickRight
val msgClickLeft = cmp(msgString("common.left"), cHighlight).append(separator) + msgClick
val msgShiftClickLeft = cmp(msgString("common.sneak"), cHighlight).append(separator) + msgClickLeft
val msgTrue = msgString("common.boolTrue")
val msgFalse = msgString("common.boolFalse")
val msgNone = msgString("common.none")
val msgNoBridge = miniMessages.deserialize("<br><dark_red>⚠</dark_red> <red>MUtils-Bridge is required to communicate with the MUtils Servers!\n" +
        " <dark_grey>↳ <grey>Download Manually:</grey> <blue><click:open_url:'https://mutils.de/download'>[Click]</click></blue>\n" +
        " ↳ <grey>Download Automatically:</grey> <blue><click:run_command:'/mutils-bridge:install'>[Click]</click></blue>\n" +
        "(Note: Some api actions requires a MUtils-Premium account)")