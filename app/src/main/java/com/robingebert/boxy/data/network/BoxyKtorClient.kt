package com.robingebert.boxy.data.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class BoxyKtorClient(
    private val getUrl: () -> String,
    private val getUsername: () -> String,
    private val getPassword: () -> String
) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }

        install(HttpSend)
    }

    init {
        client.plugin(HttpSend).intercept { request ->

            val currentUrl = getUrl()
            val currentUser = getUsername()
            val currentPass = getPassword()

            if (currentUrl.isNotBlank()) {
                val parsedUrl = Url(currentUrl)
                request.url {
                    protocol = parsedUrl.protocol
                    host = parsedUrl.host
                    port = parsedUrl.port

                    val basePath = parsedUrl.encodedPath.removeSuffix("/")
                    val requestPath = encodedPath.removePrefix("/")
                    encodedPath = if (basePath.isNotEmpty()) "$basePath/$requestPath" else requestPath
                }
            }

            if (currentUser.isNotBlank() && currentPass.isNotBlank()) {
                request.basicAuth(currentUser, currentPass)
            }

            execute(request)
        }
    }
}