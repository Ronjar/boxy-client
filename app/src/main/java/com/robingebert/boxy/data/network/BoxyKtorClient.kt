package com.robingebert.boxy.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.basicAuth
import io.ktor.serialization.kotlinx.json.json

class BoxyKtorClient {

    var url: String? = null

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            url("https://xample.com/")
            basicAuth("username", "password")
        }
    }
}