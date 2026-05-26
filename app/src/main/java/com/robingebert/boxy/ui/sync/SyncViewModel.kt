package com.robingebert.boxy.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robingebert.boxy.data.DataStoreManager
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.domain.SyncRepository
import com.robingebert.boxy.domain.VersionInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SyncViewModel(
    private val dataStoreManager: DataStoreManager,
    private val syncRepository: SyncRepository
) : ViewModel() {

    val url = dataStoreManager.url.flow
        .stateIn(viewModelScope, SharingStarted.Lazily, "")
    val username = dataStoreManager.username.flow
        .stateIn(viewModelScope, SharingStarted.Lazily, "")
    val password = dataStoreManager.password.flow
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val pulledVersion = dataStoreManager.pulledVersion.flow
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    private val _versions = MutableStateFlow<DataFetcher<List<VersionInfo>>>(DataFetcher.Fetching)
    val versions = _versions
        .onSubscription {
            getVersionsList()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = DataFetcher.Fetching
        )

    private val _latestVersion = MutableStateFlow<DataFetcher<VersionInfo>>(DataFetcher.Fetching)
    val latestVersion = _latestVersion
        .onSubscription {
            getLatestVersionTag()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = DataFetcher.Fetching
        )

    fun setCredentials(url: String, username: String, password: String) {
        dataStoreManager.url.set(url)
        dataStoreManager.username.set(username)
        dataStoreManager.password.set(password)
    }

    fun getVersionsList() {
        viewModelScope.launch {
            syncRepository.getVersionsList().onSuccess {
                _versions.value = DataFetcher.Data(it)
            }
        }
    }

    fun getLatestVersionTag() {
        viewModelScope.launch {
            syncRepository.getLatestVersionTag().fold(
                onSuccess = {
                    _latestVersion.value = DataFetcher.Data(it)
                },
                onFailure = {
                    _latestVersion.value = DataFetcher.Error(it)
                }
            )
        }
    }


    fun pullLatestVersion() {
        viewModelScope.launch {
            syncRepository.pullLatestVersion()
        }
    }

    fun pullVersion(version: String) {
        viewModelScope.launch {
            syncRepository.pullVersion(version)
        }
    }

    fun pushNewVersion() {
        viewModelScope.launch {
            syncRepository.pushCurrentState()
        }
    }
}