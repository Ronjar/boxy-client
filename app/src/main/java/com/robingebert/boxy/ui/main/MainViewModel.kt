package com.robingebert.boxy.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robingebert.boxy.data.DataStoreManager
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.data.network.SyncApi
import com.robingebert.boxy.domain.UpdateRepository
import com.robingebert.boxy.ui.common.Event
import com.robingebert.boxy.ui.common.SnackbarController
import com.robingebert.boxy.ui.common.SnackbarEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
    val dataStoreManager: DataStoreManager,
    val syncApi: SyncApi,
    val updateRepository: UpdateRepository,
    val snackbarController: SnackbarController
) :
    ViewModel() {

    val localChanges = dataStoreManager.localChanges.flow
        .stateIn(viewModelScope, SharingStarted.Lazily, false)
    val pulledVersion = dataStoreManager.pulledVersion.flow
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    private val _remoteChanges = MutableStateFlow<DataFetcher<Boolean>>(DataFetcher.Fetching)
    val remoteChanges = _remoteChanges
        .stateIn(viewModelScope, SharingStarted.Lazily, DataFetcher.Fetching)

    init {
        viewModelScope.launch {
            pulledVersion.collect {
                _remoteChanges.value = compareWithRemoteVersion(it)
            }
        }
        checkForUpdates()
    }

    suspend fun compareWithRemoteVersion(localVersion: String): DataFetcher<Boolean> {
        return syncApi.getLatestVersionTag().fold(
            onSuccess = { latestVersion ->
                DataFetcher.Data(latestVersion != localVersion)
            },
            onFailure = {
                snackbarController.sendEvent(
                    SnackbarEvent.SnackbarException(throwable = it as Exception)
                )
                DataFetcher.Error(it)
            }
        )
    }

    //region Updates

    private val _updateDownloadProgress = MutableStateFlow(-1f)
    val updateDownloadProgress = _updateDownloadProgress
        .stateIn(viewModelScope, SharingStarted.Lazily, -1f)
    private var downloadedApk: File? = null

    fun checkForUpdates() {
        viewModelScope.launch {
            updateRepository.checkForVersion().fold(
                onSuccess = {
                    if (it != null) {
                        snackbarController.sendEvent(
                            SnackbarEvent.SnackbarMessage(
                                message = Event.UpdateAvailable {
                                    downloadWithProgress(it.downloadUrl)
                                }
                            )
                        )
                    }
                },
                onFailure = { e ->
                    snackbarController.sendEvent(
                        SnackbarEvent.SnackbarException(throwable = e as Exception)
                    )
                }
            )
        }
    }

    private fun downloadWithProgress(url: String) {
        viewModelScope.launch {
            updateRepository.downloadApkWithProgress(
                apkUrl = url,
                onProgress = { progress ->
                    _updateDownloadProgress.value = progress
                },
                onComplete = { file ->
                    downloadedApk = file
                    viewModelScope.launch {
                        snackbarController.sendEvent(
                            SnackbarEvent.SnackbarMessage(
                                message = Event.UpdateInstallable {
                                    updateRepository.installApk(file)
                                }
                            )
                        )
                    }
                }
            )
        }
    }
    fun downloadButtonClicked() {
        downloadedApk?.let {
            updateRepository.installApk(it)
        }
    }

    fun setCredentials(url: String, username: String, password: String) {
        dataStoreManager.url.set(url)
        dataStoreManager.username.set(username)
        dataStoreManager.password.set(password)
    }
}