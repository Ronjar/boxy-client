package com.robingebert.boxy.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robingebert.boxy.data.DataStoreManager
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.domain.AssetRepository
import com.robingebert.boxy.domain.LocationRepository
import com.robingebert.boxy.domain.models.Asset
import com.robingebert.boxy.domain.models.Location
import com.robingebert.boxy.domain.models.LocationNode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList


sealed interface UpNavigationTarget {
    data object Home : UpNavigationTarget
    data class Folder(val location: Location) : UpNavigationTarget
}

class OverviewViewModel(
    private val dataStoreManager: DataStoreManager,
    private val locationRepository: LocationRepository,
    private val assetRepository: AssetRepository
) : ViewModel() {

    private val _currentParent = MutableStateFlow<Location?>(null)
    val currentParent: StateFlow<Location?> = _currentParent.asStateFlow()
    val currentGrandParent: StateFlow<Location?> = _currentParent

    val hasParentLocation: Boolean get() = _currentParent.value != null

    private val _breadcrumbs = MutableStateFlow<List<Location>>(emptyList())
    val breadcrumbs: StateFlow<List<Location>> = _breadcrumbs.asStateFlow()

    fun navigateDown(location: Location) {
        val currentPath = _breadcrumbs.value.toMutableList()
        currentPath.add(location)
        _breadcrumbs.value = currentPath
        _currentParent.value = location
    }

    fun navigateUp() {
        val currentPath = _breadcrumbs.value.toMutableList()
        if (currentPath.isNotEmpty()) {
            currentPath.removeAt(currentPath.lastIndex)
            _breadcrumbs.value = currentPath
            _currentParent.value = currentPath.lastOrNull()
        }
    }

    val upNavigationTarget: StateFlow<UpNavigationTarget?> = _breadcrumbs
        .map { path ->
            when (path.size) {
                0 -> null
                1 -> UpNavigationTarget.Home
                else -> {
                    val grandParent = path[path.size - 2]
                    UpNavigationTarget.Folder(grandParent)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


    fun changed(){
        dataStoreManager.localChanges.set(true)
    }

    //region Assets
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentAssets: StateFlow<DataFetcher<List<Asset>>> = _currentParent
        .flatMapLatest { parent ->
            if (parent == null) {
                flowOf(DataFetcher.Data(emptyList()))
            } else {
                assetRepository.getAssetsForLocationId(parent.id)
                    .map { assets ->
                        DataFetcher.Data(assets) as DataFetcher<List<Asset>>
                    }
                    .onStart {
                        emit(DataFetcher.Fetching)
                    }
                    .catch {
                        emit(DataFetcher.Data(emptyList()))
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DataFetcher.Fetching
        )

    fun newAsset(): Asset? {
        return _currentParent.value?.let {
            Asset(
                id = assetRepository.size().toLong(),
                name = "",
                parentId = it.id
            )
        }
    }

    fun saveAsset(asset: Asset) {
        viewModelScope.launch {
            assetRepository.upsert(asset)
            changed()
        }
    }

    fun removeAsset(asset: Asset) {
        viewModelScope.launch {
            assetRepository.remove(asset.id)
            changed()
        }
    }

    //endregion

    //region Locations
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentLocations: StateFlow<DataFetcher<List<LocationNode>>> = _currentParent
        .flatMapLatest { parent ->
            locationRepository.tree(parent?.id)
                .map { DataFetcher.Data(it) as DataFetcher<List<LocationNode>> }
                .onStart { emit(DataFetcher.Fetching) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DataFetcher.Fetching
        )

    fun newLocation(): Location {
        return Location(
            id = locationRepository.size().toLong(),
            name = "",
            parentId = _currentParent.value?.id,
            picture = null,
            updated = System.currentTimeMillis().toString()
        )
    }

    fun saveLocation(location: Location) {
        viewModelScope.launch {
            locationRepository.upsert(location)
            changed()
        }
    }

    fun removeLocation(location: Location) {
        viewModelScope.launch {
            locationRepository.remove(location.id)
            changed()
        }
    }
    //endregion
}