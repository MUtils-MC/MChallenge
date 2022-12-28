@file:Suppress("PropertyName")

package de.miraculixx.mutils.api

import de.miraculixx.mutils.extensions.UUIDExtension
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ModuleVersion(val latest: Int, val last: Int)

@Serializable
data class AccountData(var key: String = "", var uuid: @Serializable(with = UUIDExtension::class)UUID? = null, var autoUpdate: Boolean = false)

@Serializable
data class LoginResponse(val success: Boolean = false, val slots: Int = 0, val protocol: Int = 0)

/**
 * @param id Version ID
 * @param project_id Parent project ID
 * @param name Version name
 * @param version_number Version number
 * @param files All version files
 */
data class ModrinthVersion(
    val id: String,
    val project_id: String,
    val name: String,
    val version_number: String,
    val files: List<ModrinthFile>
)

/**
 * @param url File URL to download
 * @param filename File name
 * @param size File size in bytes
 */
data class ModrinthFile(val url: String, val filename: String, val size: Long)