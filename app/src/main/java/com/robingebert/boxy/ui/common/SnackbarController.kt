package com.robingebert.boxy.ui.common

import com.robingebert.boxy.ui.common.composables.Event
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

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