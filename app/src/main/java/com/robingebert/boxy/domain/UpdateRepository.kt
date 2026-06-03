package com.robingebert.boxy.domain

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.FileProvider
import com.robingebert.boxy.data.network.UpdateApi
import com.robingebert.boxy.domain.models.UpdateInfo
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.onDownload
import io.ktor.client.plugins.timeout
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import java.io.File
import kotlin.time.Duration.Companion.minutes

class UpdateRepository(
    private val updateApi: UpdateApi,
    private val context: Context
) {
    suspend fun checkForVersion(): Result<UpdateInfo?> {
        val current = context.getAppVersionName()
        return updateApi.checkForVersion().fold(
            onSuccess = { latest ->
                Result.success(if (latest.version != current) latest else null)
            },
            onFailure = { e ->
                Result.failure(e)
            }
        )
    }

    suspend fun downloadApkWithProgress(
        apkUrl: String,
        onProgress: (Float) -> Unit,
        onComplete: (File) -> Unit
    ) {
        val apkFile = File(context.getExternalFilesDir(null), "update.apk")

        val client = HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
            }
        }

        client.prepareGet(
            urlString = apkUrl,
            block = {
                val timeout = 30.minutes.inWholeMilliseconds
                timeout {
                    requestTimeoutMillis = timeout
                    connectTimeoutMillis = timeout
                    socketTimeoutMillis = timeout
                }

                onDownload { bytesSentTotal, contentLength ->
                    contentLength?.let {
                        (bytesSentTotal.toFloat() / it.toFloat()).let { progress ->
                            if (progress < 1) {
                                onProgress(progress)
                            } else {
                                onComplete(apkFile)
                            }
                        }
                    }

                }
            })
            .execute { httpResponse ->
                val byteReadChannel = httpResponse.bodyAsChannel()
                byteReadChannel.copyAndClose(apkFile.writeChannel())
            }
    }

    fun installApk(file: File) {
        val apkUri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun Context.getAppVersionName(): String {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(0L)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }
            packageInfo.versionName ?: "Unknown"

        } catch (_: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }
}