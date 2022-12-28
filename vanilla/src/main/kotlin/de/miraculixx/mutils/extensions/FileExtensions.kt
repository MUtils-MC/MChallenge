package de.miraculixx.mutils.extensions

import java.io.File
import java.util.*

//private val jsonValidationObj = Regex("\\{.*.}")
//private val jsonValidationList = Regex("\\[.*.]")

fun File.readJsonString(isObject: Boolean): String {
    if (!exists()) createNewFile()
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