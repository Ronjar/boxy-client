package com.robingebert.boxy.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import java.io.File
import io.ktor.client.plugins.expectSuccess
import io.ktor.http.isSuccess
import kotlin.coroutines.cancellation.CancellationException

class NetworkException(cause: Throwable) : Exception(cause)
class HttpException(statusCode: Int) : Exception("HTTP $statusCode")

class StorageApi(private val client: HttpClient) {

    suspend fun downloadLatestVersion(): Result<ByteArray> {
        return try {
            val response = client.get("") {
                expectSuccess = false
            }

            if (response.status.isSuccess()) {
                val bytes = response.body<ByteArray>()
                Result.success(bytes)
            } else {
                Result.failure(HttpException(response.status.value))
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(NetworkException(e))
        }
    }

    suspend fun getVersionsList(): Result<List<String>> {
        return client.safeGet("versions")
    }

    suspend fun getLatestVersionTag(): Result<String> {
        return client.safeGet<String>("latest")
    }

    suspend fun downloadTaggedVersion(versionTag: String): ByteArray {
        return client.get(versionTag).body()
    }

    suspend fun uploadNewVersion(zipFile: File): HttpResponse {
        return client.post {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", zipFile.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "application/zip")
                            append(HttpHeaders.ContentDisposition, "filename=\"${zipFile.name}\"")
                        })
                    }
                ))
        }
    }
}