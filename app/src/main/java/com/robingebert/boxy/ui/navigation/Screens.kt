package com.robingebert.boxy.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Main : Screen()
    @Serializable
    data object Auth : Screen()
    @Serializable
    data object Settings : Screen()
}
