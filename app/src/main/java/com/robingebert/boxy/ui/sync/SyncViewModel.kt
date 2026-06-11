package com.robingebert.boxy.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robingebert.boxy.data.DataStoreManager
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.ui.common.SnackbarController
import com.robingebert.boxy.ui.common.SnackbarEvent
import com.robingebert.boxy.domain.SyncRepository
import com.robingebert.boxy.domain.VersionInfo
import com.robingebert.boxy.ui.common.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SyncViewModel(
    private val dataStoreManager: DataStoreManager,
    private val syncRepository: SyncRepository,
    private val snackbarController: SnackbarController
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
            syncRepository.pullLatestVersion().fold(
                onSuccess = {
                    dataStoreManager.localChanges.set(false)
                    dataStoreManager.pulledVersion.set(_latestVersion.value.dataOrNull()?.id ?: "")
                    snackbarController.sendEvent(
                        SnackbarEvent.SnackbarMessage(
                            message = Event.RestoreSuccess
                        )
                    )
                },
                onFailure = {
                    snackbarController.sendEvent(
                        SnackbarEvent.SnackbarException(
                            throwable = it
                        )
                    )
                }
            )
        }
    }

    fun pullVersion(version: String) {
        viewModelScope.launch {
            syncRepository.pullVersion(version).fold(
                onSuccess = {
                    dataStoreManager.localChanges.set(false)
                    dataStoreManager.pulledVersion.set(it)
                    snackbarController.sendEvent(
                        SnackbarEvent.SnackbarMessage(
                            message = Event.RestoreSuccess
                        )
                    )
                },
                onFailure = {
                    snackbarController.sendEvent(
                        SnackbarEvent.SnackbarException(
                            throwable = it
                        )
                    )
                }
            )
        }
    }

    fun pushNewVersion() {
        viewModelScope.launch {
            syncRepository.pushCurrentState().fold(
                onSuccess = {
                    dataStoreManager.localChanges.set(false)
                    dataStoreManager.pulledVersion.set(it)
                    getVersionsList()
                    snackbarController.sendEvent(
                        SnackbarEvent.SnackbarMessage(
                            message = Event.BackupSuccess
                        )
                    )
                },
                onFailure = {
                    snackbarController.sendEvent(
                        SnackbarEvent.SnackbarException(
                            throwable = it
                        )
                    )
                }
            )
        }
    }

    fun deleteVersion(version: VersionInfo){
        viewModelScope.launch {
            syncRepository.deleteVersion(version).fold(
                onSuccess = {
                    getVersionsList()
                    snackbarController.sendEvent(
                        SnackbarEvent.SnackbarMessage(
                            message = Event.DeleteSuccess
                        )
                    )
                },
                onFailure = {
                    snackbarController.sendEvent(
                        SnackbarEvent.SnackbarException(
                            throwable = it
                        )
                    )
                }
            )
        }
    }
}