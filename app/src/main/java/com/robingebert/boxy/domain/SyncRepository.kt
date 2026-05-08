package com.robingebert.boxy.domain

import android.content.Context
import com.robingebert.boxy.data.network.StorageApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class SyncRepository(
    private val context: Context,
    private val api: StorageApi
) {
    private val filesDir = context.filesDir
    private val cacheDir = context.cacheDir

    suspend fun pullLatestVersion(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val zipData = api.downloadLatestVersion()
            clearLocalData()
            unzipToFilesDir(zipData)

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun pushCurrentState(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val tempZipFile = File(cacheDir, "upload_temp.zip")

            zipLocalData(tempZipFile)

            api.uploadNewVersion(tempZipFile)

            tempZipFile.delete()

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
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
                        throw SecurityException("Ungültiger ZIP-Pfad: ${entry.name}")
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
}