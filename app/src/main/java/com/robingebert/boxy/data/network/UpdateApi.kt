package com.robingebert.boxy.data.network

import com.robingebert.boxy.domain.models.GithubRelease
import com.robingebert.boxy.domain.models.UpdateInfo
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File


class UpdateApi{
    suspend fun checkForVersion(): Result<UpdateInfo> {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }

            install(HttpSend)
        }
        return client.safeGet<GithubRelease>("https://api.github.com/repos/Ronjar/boxy-client/releases/latest")
            .map { release ->
                val apkAsset = release.assets.find { it.name.endsWith(".apk") }
                    ?: throw IllegalStateException()

                UpdateInfo(
                    version = release.tagName,
                    downloadUrl = apkAsset.browserDownloadUrl
                )
            }
    }
}