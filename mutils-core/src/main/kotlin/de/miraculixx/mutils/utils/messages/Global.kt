package de.miraculixx.mutils.utils.messages

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.format.NamedTextColor

const val mhAPI = "https://minecraft-heads.com/scripts/api.php?"
const val namespace = "de.miraculixx.api"
var consoleAudience: Audience? = null
var debug = false

val prefix = cmp("Headifier", cHighlight) + cmp(" >> ", NamedTextColor.DARK_GRAY)
fun invalidConfig(key: String) = prefix + cmp("Configuration section '$key' is invalid!", cError)