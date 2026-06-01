package com.robingebert.boxy.data.network

sealed class DataFetcher<out T> {
    data class Data<T>(val data: T) : DataFetcher<T>()
    data object Fetching: DataFetcher<Nothing>()
    data class Error(val message: Throwable): DataFetcher<Nothing>()

    fun dataOrNull(): T? {
        return when(this) {
            is Data -> data
            else -> null
        }
    }
}