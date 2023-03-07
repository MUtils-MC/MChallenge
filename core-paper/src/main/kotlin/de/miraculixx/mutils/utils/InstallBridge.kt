package de.miraculixx.mutils.utils

import de.miraculixx.mutils.messages.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.bukkit.plugin.PluginManager
import java.io.File

class InstallBridge(private val moduleName: String) {
    private val client = HttpClient(CIO)

    fun verifyInstall(accessPoint: PluginManager): Boolean {
        return accessPoint.isPluginEnabled("MUtils-Bridge")
    }

    suspend fun install(accessPoint: PluginManager): Boolean {
        val response: HttpResponse = client.get("https://mutils.de/files/bridge") {
            header("module", moduleName) //Logging how much traffic each module produce
        }
        val data = response.body() as ByteArray
        val destination = File("/plugins/update/MUtils-Bridge.jar")
        destination.parentFile.mkdir()
        destination.writeBytes(data)
        return try {
            accessPoint.loadPlugin(destination)
            accessPoint.isPluginEnabled("MUtils-Bridge")
        } catch (e: Exception) {
            consoleAudience.sendMessage(prefix + cmp("Failed to automatically enable MUtils-Bridge! Please restart your server to enable all functions!", cError))
            false
        }
    }
}