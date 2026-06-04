package com.robingebert.boxy.data.network

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.streams.asInput
import java.io.File

class NetworkException(cause: Throwable) : Exception(cause)
class HttpException(statusCode: Int) : Exception(statusCode.toString())

class SyncApi(private val client: HttpClient) {

    suspend fun downloadLatestVersion(): Result<ByteArray> {
        return client.safeGet<ByteArray>("")
    }

    suspend fun getVersionsList(): Result<List<String>> {
        return client.safeGet<List<String>>("versions")
    }

    suspend fun getLatestVersionTag(): Result<String> {
        return client.safeGet<String>("latest")
    }

    suspend fun downloadTaggedVersion(versionTag: String): Result<ByteArray> {
        return client.safeGet<ByteArray>(versionTag)
    }

    suspend fun uploadNewVersion(zipFile: File): Result<String> {
        return client.safePost<String, MultiPartFormDataContent>(
            urlString = "",
            bodyData = MultiPartFormDataContent(
                formData {
                    append("file", InputProvider(zipFile.length()) {
                        zipFile.inputStream().asInput()
                    }, Headers.build {
                        append(HttpHeaders.ContentType, "application/zip")
                        append(HttpHeaders.ContentDisposition, "filename=\"${zipFile.name}\"")
                    })
                }
            ))
    }

    suspend fun deleteVersion(versionTag: String): Result<Unit> {
        return client.safeDelete<Unit>("") {
            url {
                parameters.append("version", versionTag)
            }
        }
    }
}