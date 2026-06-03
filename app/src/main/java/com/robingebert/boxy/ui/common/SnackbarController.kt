package com.robingebert.boxy.ui.common

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


sealed class Event{
    object BackupSuccess : Event()
    object RestoreSuccess : Event()
    object DeleteSuccess : Event()
    data class UpdateAvailable(val onClick: () -> Unit) : Event()
    data class UpdateInstallable(var onClick: () -> Unit) : Event()
}

sealed class SnackbarEvent {
    data class SnackbarMessage(val message: Event) : SnackbarEvent()
    data class SnackbarException(val throwable: Throwable): SnackbarEvent()
}

class SnackbarController {
    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }
}