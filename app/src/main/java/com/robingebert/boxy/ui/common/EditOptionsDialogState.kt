package com.robingebert.boxy.ui.common

sealed class EditOptionsDialogState<out T> {
    data class Edit<T>(val data: T) : EditOptionsDialogState<T>()
    data class Options<T>(val data: T) : EditOptionsDialogState<T>()
    data object None : EditOptionsDialogState<Nothing>()
}