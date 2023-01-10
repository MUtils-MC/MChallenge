package de.miraculixx.mutils.api

import de.miraculixx.mutils.messages.cmp
import de.miraculixx.mutils.messages.consoleAudience
import de.miraculixx.mutils.messages.debug
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.net.URL

object WebClient {
    private val client = HttpClient(CIO)

    suspend fun getString(destination: String): String {
        return try {
            val url = URL(destination)
            val response: HttpResponse = WebClient.client.get(url)
            response.bodyAsText()
        } catch (e: Exception) {
            if (debug) consoleAudience.sendMessage(cmp("Failed to resolve $destination"))
            return ""
        }
    }

    suspend fun getFile(destination: String): ByteArray? {
        return try {
            val url = URL(destination)
            val response: HttpResponse = WebClient.client.get(url)
            response.body() as ByteArray
        } catch (e: Exception) {
            if (debug) consoleAudience.sendMessage(cmp("Failed to resolve $destination"))
            return null
        }
    }
}