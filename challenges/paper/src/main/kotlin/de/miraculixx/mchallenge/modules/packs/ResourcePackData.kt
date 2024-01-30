package de.miraculixx.mchallenge.modules.packs

import de.miraculixx.mbridge.WebClient
import de.miraculixx.mvanilla.messages.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

suspend fun getModrinthVersion(modrinthVersionID: String): ModrinthVersion? {
    val body = WebClient.get("https://api.modrinth.com/v2/version/$modrinthVersionID")
    return try {
        json.decodeFromString<ModrinthVersion>(body)
    } catch (e: Exception) {
        consoleAudience.sendMessage(prefix + cmp("Failed to resolve modrinth version $modrinthVersionID!", cError))
        consoleAudience.sendMessage(prefix + cmp("Error: ${e.message ?: "Unknown"}", cError))
        return null
    }
}

@Serializable
data class ModrinthVersion(
    val name: String, @SerialName("version_number")
    val versionNumber: String,
    val files: List<ModrinthFile>
)

@Serializable
data class ModrinthFile(val hashes: ModrinthFileHash, val url: String, val primary: Boolean)

@Serializable
data class ModrinthFileHash(val sha512: String, val sha1: String)