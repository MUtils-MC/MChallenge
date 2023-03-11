package de.miraculixx.mutils.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class APICheck {
    private val client = HttpClient(CIO)

    suspend fun checkIsBeta(): Boolean {
        val response = client.get("http://mutils.de/api/beta")
        val body = response.bodyAsText()
        println(body)
        return body == "true"
    }
}