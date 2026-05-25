package com.robingebert.boxy.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robingebert.boxy.data.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainViewModel(val dataStoreManager: DataStoreManager): ViewModel() {


    val localChanges = dataStoreManager.localChanges.flow
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun setCredentials(url: String, username: String, password: String) {
        dataStoreManager.url.set(url)
        dataStoreManager.username.set(username)
        dataStoreManager.password.set(password)
    }
}