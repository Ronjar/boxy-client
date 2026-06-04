package com.robingebert.boxy.domain

import android.content.Context
import com.robingebert.boxy.data.network.HttpException
import com.robingebert.boxy.data.network.NetworkException
import com.robingebert.boxy.data.network.SyncApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.time.Clock
import kotlin.time.Instant


@Serializable
data class VersionInfo(
    val id: String,
    val user: String,
    val date: Instant
) {
    fun getSince() = Clock.System.now().minus(date)
}

class SyncRepository(
    context: Context,
    private val api: SyncApi,
    private val assetRepository: AssetRepository,
    private val locationRepository: LocationRepository
) {
    private val filesDir = context.filesDir
    private val cacheDir = context.cacheDir

    suspend fun getVersionsList(): Result<List<VersionInfo>> {
        return api.getVersionsList().fold(
            onSuccess = { versionStrings ->
                Result.success(versionStrings.map { convertToVersionInfo(it) }.sortedBy { it.date }.reversed())
            },
            onFailure = { e ->
                return when (e) {
                    is HttpException if e.message.equals("404") -> {
                        Result.failure(NoBackupsYetException())
                    }

                    is NetworkException -> {
                        Result.failure(NetworkException())
                    }

                    else -> {
                        Result.failure(RestoreFailedException())
                    }
                }
            }
        )
    }

    suspend fun getLatestVersionTag(): Result<VersionInfo> {
        return api.getLatestVersionTag().fold(
            onSuccess = { versionString ->
                Result.success(convertToVersionInfo(versionString))
            },
            onFailure = { e ->
                return when (e) {
                    is HttpException if e.message.equals("404") -> {
                        Result.failure(NoBackupsYetException())
                    }

                    is NetworkException -> {
                        Result.failure(NetworkException())
                    }

                    else -> {
                        Result.failure(RestoreFailedException())
                    }
                }
            }
        )
    }

    suspend fun pullVersion(version: String): Result<String> = withContext(Dispatchers.IO) {
        api.downloadTaggedVersion(version).fold(
            onSuccess = { zipData ->
                replaceVersion(zipData)
                Result.success(version)
            },
            onFailure = { e ->
                when (e) {
                    is HttpException if e.message.equals("404") -> {
                        Result.failure(VersionNotFoundException())
                    }

                    is NetworkException -> {
                        Result.failure(NetworkException())
                    }

                    else -> {
                        Result.failure(RestoreFailedException())
                    }
                }
            }
        )
    }

    suspend fun pullLatestVersion(): Result<Unit> = withContext(Dispatchers.IO) {
        api.downloadLatestVersion().fold(
            onSuccess = { zipData ->
                replaceVersion(zipData)
                Result.success(Unit)
            },
            onFailure = { e ->
                when (e) {
                    is HttpException if e.message.equals("404") -> {
                        Result.failure(VersionNotFoundException())
                    }

                    is NetworkException -> {
                        Result.failure(NetworkException())
                    }

                    else -> {
                        Result.failure(RestoreFailedException())
                    }
                }
            }
        )
    }

    suspend fun pushCurrentState(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val tempZipFile = File(cacheDir, "upload_temp.zip")

            zipLocalData(tempZipFile)

            api.uploadNewVersion(tempZipFile).fold(
                onSuccess = { versionTag ->
                    Result.success(versionTag)
                },
                onFailure = { e ->
                    when (e) {
                        is NetworkException -> {
                            tempZipFile.delete()
                            Result.failure(NetworkException())
                        }

                        else -> {
                            tempZipFile.delete()
                            Result.failure(BackupFailedException())
                        }
                    }
                }
            )

        } catch (_: Exception) {
            Result.failure(BackupFailedException())
        }
    }

    suspend fun deleteVersion(version: VersionInfo): Result<Unit> {
        return api.deleteVersion(version.id).fold(
            onSuccess = {
                Result.success(Unit)
            },
            onFailure = { e ->
                when (e) {
                    is HttpException if e.message.equals("404") -> {
                        Result.failure(VersionNotFoundException())
                    }

                    is NetworkException -> {
                        Result.failure(NetworkException())
                    }

                    else -> {
                        Result.failure(e)
                    }
                }
            }
        )
    }

    private fun replaceVersion(zipData: ByteArray) {
        clearLocalData()
        unzipToFilesDir(zipData)
        assetRepository.refresh()
        locationRepository.refresh()
    }

    private fun clearLocalData() {
        File(filesDir, "assets.json").delete()
        File(filesDir, "locations.json").delete()

        val imagesDir = File(filesDir, "images")
        if (imagesDir.exists()) {
            imagesDir.listFiles()?.forEach { it.delete() }
        }
    }

    private fun unzipToFilesDir(zipBytes: ByteArray) {
        ByteArrayInputStream(zipBytes).use { bais ->
            ZipInputStream(bais).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val outFile = File(filesDir, entry.name)

                    if (!outFile.canonicalPath.startsWith(filesDir.canonicalPath)) {
                        throw SecurityException("Invalid Zip-Path: ${entry.name}")
                    }

                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()

                        FileOutputStream(outFile).use { fos ->
                            zis.copyTo(fos)
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        }
    }

    private fun zipLocalData(outFile: File) {
        ZipOutputStream(FileOutputStream(outFile)).use { zos ->
            addFileToZip(File(filesDir, "assets.json"), "assets.json", zos)
            addFileToZip(File(filesDir, "locations.json"), "locations.json", zos)

            val imagesDir = File(filesDir, "images")
            if (imagesDir.exists() && imagesDir.isDirectory) {
                imagesDir.listFiles()?.forEach { file ->
                    addFileToZip(file, "images/${file.name}", zos)
                }
            }
        }
    }

    private fun addFileToZip(file: File, entryName: String, zos: ZipOutputStream) {
        if (!file.exists()) return

        FileInputStream(file).use { fis ->
            val entry = ZipEntry(entryName)
            zos.putNextEntry(entry)
            fis.copyTo(zos)
            zos.closeEntry()
        }
    }

    private fun convertToVersionInfo(versionString: String): VersionInfo {
        val parts = versionString.split("_")
        return VersionInfo(
            id = versionString,
            user = parts[0],
            date = Instant.fromEpochSeconds(parts[1].toLong())
        )
    }
}