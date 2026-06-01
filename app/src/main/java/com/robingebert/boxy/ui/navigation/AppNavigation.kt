package com.robingebert.boxy.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.robingebert.boxy.ui.sync.screen.SyncScreen
import com.robingebert.boxy.ui.overview.OverviewScreen
import com.robingebert.boxy.ui.settings.SettingsScreen

data class Destination(val name: String = "", val isMain: Boolean = true)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(navController: NavHostController, onDestinationChanged: (Destination) -> Unit) {

    NavHost(navController, startDestination = Screen.Main) {
        composable<Screen.Main> {
            onDestinationChanged(
                Destination(
                    name = "Boxy",
                    isMain = true
                )
            )
            OverviewScreen()
        }
        composable<Screen.Settings> {
            onDestinationChanged(
                Destination(
                    name = "Settings",
                    isMain = false
                )
            )
            SettingsScreen()
        }
        composable<Screen.Sync> {
            onDestinationChanged(
                Destination(
                    name = "Sync",
                    isMain = false
                )
            )
            SyncScreen()
        }
    }


}