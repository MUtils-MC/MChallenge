package de.miraculixx.mvanilla.extensions

import net.kyori.adventure.audience.Audience
import org.bukkit.entity.Player
import java.io.File
import java.util.*

fun File.readJsonString(isObject: Boolean): String {
    if (!exists()) {
        parentFile.mkdirs()
        createNewFile()
    }
    val outPut = readText()
    return if (isObject)
        if (outPut.startsWith('{') && outPut.endsWith('}')) outPut
        else {
            writeText("{}")
            "{}"
        }
    else if (outPut.startsWith('[') && outPut.endsWith(']')) outPut
    else {
        writeText("[]")
        "[]"
    }
}

fun String.toUUID(): UUID? {
    return try {
        UUID.fromString(this)
    } catch (_: IllegalArgumentException) {
        null
    }
}

fun Boolean.toggle(player: Player): Boolean {
    return if (this) {
        player.soundDisable()
        false
    } else {
        player.soundEnable()
        true
    }
}