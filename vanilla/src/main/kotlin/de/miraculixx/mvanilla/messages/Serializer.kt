package de.miraculixx.mvanilla.messages

import kotlinx.serialization.json.Json
import net.kyori.adventure.text.flattener.ComponentFlattener
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

val plainSerializer = PlainTextComponentSerializer.builder().flattener(ComponentFlattener.textOnly()).build()
val miniMessages = MiniMessage.miniMessage()
val jsonSerializer = GsonComponentSerializer.gson()
val legacySerializer = LegacyComponentSerializer.builder()
    .character('§')
    .hexColors()
    .useUnusualXRepeatedCharacterHexFormat()
    .build()

val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

val jsonCompact = Json {
    ignoreUnknownKeys = true
}