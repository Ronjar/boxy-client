package com.robingebert.boxy.ui.main

import androidx.lifecycle.ViewModel
import com.robingebert.boxy.data.DataStoreManager

class MainViewModel(val dataStoreManager: DataStoreManager): ViewModel() {

    fun setCredentials(url: String, username: String, password: String) {
        dataStoreManager.url.set(url)
        dataStoreManager.username.set(username)
        dataStoreManager.password.set(password)
    }
}