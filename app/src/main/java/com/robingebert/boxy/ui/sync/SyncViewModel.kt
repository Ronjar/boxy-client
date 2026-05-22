package com.robingebert.boxy.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robingebert.boxy.data.DataStoreManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn

class SyncViewModel(private val dataStoreManager: DataStoreManager): ViewModel() {

    val url = dataStoreManager.url.flow
        .stateIn(viewModelScope, SharingStarted.Lazily, "")
    val username = dataStoreManager.username.flow
        .stateIn(viewModelScope, SharingStarted.Lazily, "")
    val password = dataStoreManager.password.flow
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    fun setCredentials(url: String, username: String, password: String) {
        dataStoreManager.url.set(url)
        dataStoreManager.username.set(username)
        dataStoreManager.password.set(password)
    }
}