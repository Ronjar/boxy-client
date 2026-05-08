package com.robingebert.boxy.domain

import android.content.Context
import com.robingebert.boxy.domain.models.Asset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class AssetRepository(context: Context) {
    private val file = File(context.filesDir, "assets.json")
    private val mutex = Mutex()
    private val assets = mutableListOf<Asset>()

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    init {
        if (file.exists()) {
            try {
                val data = file.readText()
                assets.addAll(json.decodeFromString<List<Asset>>(data))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun saveToFile() = withContext(Dispatchers.IO) {
        val jsonString = json.encodeToString(assets)
        file.writeText(jsonString)
    }

    suspend fun getAll(): List<Asset> = mutex.withLock {
        return assets.toList()
    }

    suspend fun upsert(asset: Asset) = mutex.withLock {
        val index = assets.indexOfFirst { it.id == asset.id }
        if (index != -1) {
            assets[index] = asset
            saveToFile()
        } else {
            assets.add(asset)
        }
        saveToFile()
    }

    suspend fun remove(id: Long) = mutex.withLock {
        val removed = assets.removeAll { it.id == id }
        if (removed) {
            saveToFile()
        }
    }

    fun size(): Int {
        return assets.size
    }

    suspend fun getAssetsForLocationId(locationId: Long): List<Asset> = mutex.withLock {
        return assets.filter { it.parentId == locationId }
    }
}