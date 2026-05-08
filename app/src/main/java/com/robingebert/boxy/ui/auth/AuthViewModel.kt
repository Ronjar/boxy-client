package com.robingebert.boxy.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robingebert.boxy.data.DataStoreManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val dataStoreManager: DataStoreManager): ViewModel() {

    sealed class UiEvent {
        data object SuccessfulLogin : UiEvent()
        data class LoginError(val error: String) : UiEvent()
    }

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    fun login(username: String, password: String) {

    }
}