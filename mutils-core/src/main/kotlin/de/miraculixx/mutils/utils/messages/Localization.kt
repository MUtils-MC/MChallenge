package de.miraculixx.mutils.utils.messages

import kotlinx.serialization.Serializable

/**
 * The map where all localization strings are cached.
 *
 * [String] = Text Key
 *
 * [TextValue] = Text Value
 */
var localization: Map<String, TextValue> = emptyMap()

/**
 * Get a translation for the given key. If no translation were found the key will be returned in red.
 * @param key Localization Key
 * @param input Input variables. <input-i>
 */
fun msg(key: String, input: List<String> = emptyList()) = miniMessages.deserialize("<!i>" + (localization[key]?.singleLine?.replaceInput(input) ?: "<red>$key"))

/**
 * Get a translation for the given key. If no translation were found the key will be returned in red.
 * @param key Localization Key
 * @param input Input variables. <input-i>
 * @param inline Inline string before every line (useful for listing)
 */
fun msgList(key: String, input: List<String> = emptyList(), inline: String = "   ") = localization[key]?.multiLine?.map {
        miniMessages.deserialize(inline + "<!i>" + it.replaceInput(input))
    }?.ifEmpty {
        listOf(cmp(inline + key, cError))
    } ?: listOf(cmp(inline + key, cError))

private fun String.replaceInput(input: List<String>): String {
    var msg = this
    input.forEachIndexed { index, s -> msg = msg.replace("<input-${index.plus(1)}>", s) }
    return msg
}

@Serializable
data class TextValue(val singleLine: String, val multiLine: List<String>)