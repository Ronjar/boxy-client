package com.robingebert.boxy.domain

import android.content.Context
import com.robingebert.boxy.domain.models.Asset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class AssetRepository(context: Context) {
    private val file = File(context.filesDir, "assets.json")
    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    private val _assets = MutableStateFlow<List<Asset>>(emptyList())
    val assets: StateFlow<List<Asset>> = _assets.asStateFlow()

    init {
        refresh()
    }

    fun refresh(){
        if (file.exists()) {
            try {
                val data = file.readText()
                _assets.value = json.decodeFromString<List<Asset>>(data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun saveToFile() = withContext(Dispatchers.IO) {
        val jsonString = json.encodeToString(_assets.value)
        file.writeText(jsonString)
    }

    suspend fun getById(id: Long): Asset? = mutex.withLock {
        return _assets.value.find { it.id == id }
    }

    suspend fun upsert(asset: Asset) = mutex.withLock {
        val index = _assets.value.indexOfFirst { it.id == asset.id }
        if (index != -1) {
            _assets.value = _assets.value.toMutableList().also {
                it[index] = asset
            }.toList()
        } else {
            _assets.value += asset
        }
        saveToFile()
    }

    suspend fun remove(id: Long) = mutex.withLock {
        val assetToRemove = _assets.value.find { it.id == id }
        assetToRemove?.let {
            _assets.value -= it
            saveToFile()
        }
    }

    fun size(): Int {
        return _assets.value.size
    }

    fun getAssetsForLocationId(locationId: Long): Flow<List<Asset>> {
        return _assets.map { allAssets ->
            allAssets.filter { it.parentId == locationId }
        }
    }
}