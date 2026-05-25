package com.robingebert.boxy.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.robingebert.boxy.ui.sync.SyncScreen
import com.robingebert.boxy.ui.overview.OverviewScreen
import com.robingebert.boxy.ui.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(navController, startDestination = Screen.Main) {
        composable<Screen.Main> {
            OverviewScreen()
        }
        composable<Screen.Settings> { SettingsScreen() }
        composable<Screen.Sync> { SyncScreen() }
    }

}