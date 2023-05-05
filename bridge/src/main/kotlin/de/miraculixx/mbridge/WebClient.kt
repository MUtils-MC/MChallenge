package de.miraculixx.mbridge

import de.miraculixx.mvanilla.messages.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.net.URL

object WebClient {
    private val client = HttpClient(CIO)

    suspend fun get(destination: String, headers: Map<String, String> = emptyMap(), body: String? = null): String {
        return try {
            proceedCall(destination, headers, body).bodyAsText()
        } catch (e: Exception) {
            if (debug) consoleAudience.sendMessage(cmp("Failed to resolve $destination"))
            return ""
        }
    }

    suspend fun getFile(destination: String, headers: Map<String, String> = emptyMap(), body: String? = null): ByteArray? {
        return try {
            proceedCall(destination, headers, body).body() as ByteArray
        } catch (e: Exception) {
            if (debug) consoleAudience.sendMessage(cmp("Failed to resolve $destination (file)"))
            return null
        }
    }

    private suspend fun proceedCall(
        destination: String,
        headers: Map<String, String> = emptyMap(),
        body: String? = null,
    ): HttpResponse = client.get(URL(destination)) {
        //if (debug) consoleAudience.sendMessage(prefix + cmp("DEBUG BRIDGE - $destination ($headers)"))
        header("User-Agent", "MUtils-API-1.1")
        headers.forEach { (key, value) -> header(key, value) }
        body?.let { setBody(it) }
    }
}