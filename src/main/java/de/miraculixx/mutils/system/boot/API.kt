@file:Suppress("SpellCheckingInspection")

package de.miraculixx.mutils.system.boot

import de.miraculixx.mutils.Main
import de.miraculixx.mutils.enums.modules.Modules
import de.miraculixx.mutils.utils.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import java.io.File
import java.net.URL
import java.util.*
import kotlin.io.path.Path

class API {
    private val client = HttpClient(CIO)

    private suspend fun get(subData: String): String {
        val url = URL("https://mutils.de/m/apiv2/public.php$subData")

        val response: HttpResponse = client.get(url)
        return response.bodyAsText()
    }

    suspend fun download(pathURL: String, pathFile: String): File {
        val url = URL(pathURL)
        val s = File.separator
        val path = Path(Main.INSTANCE.dataFolder.path + "${s}data${s}temporary$s").toAbsolutePath()
        path.toFile().mkdirs()
        val file = withContext(Dispatchers.IO) {
            File.createTempFile(UUID.randomUUID().toString(), ".mutils", path.toFile())
        }

        val response = client.get(url) {
            consoleMessage("$prefix Downloading ${contentLength()} bytes...")
        }
        val responseBody: ByteArray = response.body()
        file.writeBytes(responseBody)
        consoleMessage("$prefix Downloaded ${file.length()} bytes. Saved in temp data folder")

        file.copyTo(Path(pathFile).toAbsolutePath().toFile(), true)
        return file
    }

    suspend fun upload(file: File, name: String, key: String, uuid: String): Boolean {
        val url = "https://mutils.de/m/apiv2/z3d9Ad4Qm092L.php?key=$key&mc=$uuid&ip=$IP"
        consoleMessage("$prefix Uploading ${file.length()} bytes...")
        val response: HttpResponse = client.post(url) {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", file.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "text/yaml")
                            append(HttpHeaders.ContentDisposition, "filename=$name")
                        })
                    },
                    boundary = "WebAppBoundary"
                )
            )
        }
        return if (response.bodyAsText() == "true") {
            consoleMessage("$prefix Uploaded ${file.length()} bytes to your MUtils Cloud!")
            true
        } else {
            consoleMessage("$prefix Failed to upload selected file! ^ Reason above ^")
            false
        }
    }

    private suspend fun call(vararg calls: String): String? {
        val builder = StringBuilder("?")
        calls.forEach { builder.append("$it&") }
        val out = get(builder.toString())
        if (out.length < 2) {
            consoleWarn(
                "$prefix Can't connect to API! Please check your Server settings.",
                "$prefix If you use a free Hoster please open a ticket on our Discord. I will try to whitelist my API.",
                "$prefix Self hosting? We are welcome to help your with right proxy settings!",
                "$prefix Discord: https://mutils.de/dc"
            )
            return null
        }
        else if (out.startsWith("error")) {
            consoleWarn(
                "$prefix API returned an Error! (Error Code:${out.split('-')[1]} Call: $builder)",
                "$prefix Please contact us on our Discord to fix this or get further information.",
                "$prefix Discord: https://mutils.de/dc"
            )
            return null
        }
        return out
    }

    fun end() {
        client.close()
    }


    @kotlinx.serialization.Serializable
    data class Version(val latest: Int, val last: Int)

    fun versionCheck(version: Int, response: Version): Pair<Boolean, Boolean> {
        // Version Check
        // true -> Is up-to-date
        // false -> Is older than givin version
        return Pair(version >= response.latest, version >= response.last)
    }

    fun updatePlugin() {
        runBlocking {
            val s = File.separator
            download("https://mutils.de/m/downloads/latest", "plugins${s}update${s}MUtils.jar")
            consoleMessage("$prefix A new Version was downloaded! To install it, the server will restartet in just a moment...")
            consoleWarn("$prefix The installation only works when MUtils is named MUtils.jar")
            isUpdating = true
            Bukkit.spigot().restart()
        }
    }

    suspend fun getVersion(): Version {
        val call = call("call=version", "plugin=mutils") ?: return Version(0, 0)
        return Json.decodeFromString(call.replace(".", ""))
    }

    suspend fun login(key: String, ip: String, uuid: String, version: String, mutilsVersion: String): Boolean {
        val call = call("call=login", "key=$key", "ip=$ip", "mc=$uuid", "sv=$version", "mv=$mutilsVersion") ?: return false
        val respons = call.split('-')

        ID = respons[1].toIntOrNull() ?: 0
        return respons[0].toBoolean()
    }

    suspend fun verify(key: String, ip: String, name: String): String? {
        return call("call=activate", "key=$key", "mc=$name", "ip=$ip")
    }

    suspend fun challengeOfMonth(): Modules {
        val call = call("call=monthlychallenge") ?: "FLY"
        return try {
            Modules.valueOf(call)
        } catch (e: Exception) {
            Modules.FLY
        }
    }
}