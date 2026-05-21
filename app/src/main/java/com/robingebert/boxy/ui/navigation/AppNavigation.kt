package com.robingebert.boxy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.robingebert.boxy.ui.auth.AuthScreen
import com.robingebert.boxy.ui.overview.OverviewScreen
import com.robingebert.boxy.ui.settings.SettingsScreen

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(navController, startDestination = Screen.Main) {
        composable<Screen.Main> { OverviewScreen(
            onSettingsClicked = {
                navController.navigate(Screen.Settings)
            }
        ) }
        composable<Screen.Settings> { SettingsScreen() }
        composable<Screen.Auth> { AuthScreen() }
    }
}