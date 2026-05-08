package com.robingebert.boxy.domain

import android.content.Context
import com.robingebert.boxy.domain.models.Location
import com.robingebert.boxy.domain.models.LocationNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.collections.indexOfFirst

class LocationRepository(private val context: Context) {
    private val file = File(context.filesDir, "locations.json")
    private val imageDir = File(context.filesDir, "images")

    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    private val _locations = MutableStateFlow<List<Location>>(emptyList())
    val locations: StateFlow<List<Location>> = _locations.asStateFlow()

    init {
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }

        if (file.exists()) {
            try {
                val data = file.readText()
                _locations.value = json.decodeFromString<List<Location>>(data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun saveToFile() = withContext(Dispatchers.IO) {
        val jsonString = json.encodeToString(_locations.value)
        file.writeText(jsonString)
    }

    suspend fun getAll(): List<Location> = mutex.withLock {
        return _locations.value.toList()
    }

    suspend fun upsert(location: Location) = mutex.withLock {
        val index = _locations.value.indexOfFirst { it.id == location.id }
        if (index != -1) {
            _locations.value = _locations.value.toMutableList().also {
                it[index] = location
            }.toList()
        } else {
            _locations.value += location
        }
        saveToFile()
    }

    suspend fun remove(id: Long) = mutex.withLock {
        val locationToRemove = _locations.value.find { it.id == id }

        if (locationToRemove != null) {
            _locations.value -= locationToRemove

            locationToRemove.picture?.let { picName ->
                val imageFile = File(imageDir, picName)
                if (imageFile.exists()) {
                    imageFile.delete()
                }
            }

            saveToFile()
        }
    }

    fun size(): Int {
        return _locations.value.size
    }


    fun tree(parentId: Long? = null): Flow<List<LocationNode>> {
        return _locations.map { currentLocations ->
            val locationsByParent = currentLocations.groupBy { it.parentId }
            buildTree(parentId, locationsByParent)
        }
    }

    private fun buildTree(
        parentId: Long?,
        groupedLocations: Map<Long?, List<Location>>
    ): List<LocationNode> {
        val children = groupedLocations[parentId] ?: emptyList()

        return children.map { location ->
            LocationNode(
                location = location,
                children = buildTree(location.id, groupedLocations)
            )
        }
    }
}