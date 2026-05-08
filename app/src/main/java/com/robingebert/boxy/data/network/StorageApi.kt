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

class StorageApi(private val client: HttpClient) {

    suspend fun downloadLatestVersion(): ByteArray {
        return client.get("").body()
    }

    suspend fun getVersionsList(): List<String> {
        return client.get("versions").body()
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