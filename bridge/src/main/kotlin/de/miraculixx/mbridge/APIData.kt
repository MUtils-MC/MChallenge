@file:Suppress("PropertyName")

package de.miraculixx.mbridge

import de.miraculixx.mvanilla.data.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ModuleVersion(val latest: Int, val last: Int)

@Serializable
data class AccountData(var key: String = "", var uuid: @Serializable(with = UUIDSerializer::class)UUID? = null, var autoUpdate: Boolean = false)

@Serializable
data class LoginResponse(val success: Boolean = false, val slots: Int = 0, val protocol: Int = 0)

@Serializable
data class ModrinthVersion(val name: String, val changelog: String, val files: List<ModrinthFile>)

/**
 * @param url File URL to download
 * @param filename File name
 * @param size File size in bytes
 */
@Serializable
data class ModrinthFile(val url: String, val filename: String, val size: Long)

@Serializable
data class Styles(
    val type: String = "Unknown",
    val color: String = "",
    val colorSec: String = "",
    val name: String = "Unknown",
    val desc: String = "Unknown",
    val owned: Boolean = false,
)