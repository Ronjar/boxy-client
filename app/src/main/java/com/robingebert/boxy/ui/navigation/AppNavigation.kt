package com.robingebert.boxy.ui.navigation

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.robingebert.boxy.ui.sync.SyncScreen
import com.robingebert.boxy.ui.overview.OverviewScreen
import com.robingebert.boxy.ui.settings.SettingsScreen
import com.robingebert.boxy.ui.sync.composables.SyncBottomSheet
import com.stefanoq21.material3.navigation.BottomSheetNavigator
import com.stefanoq21.material3.navigation.ModalBottomSheetLayout
import com.stefanoq21.material3.navigation.bottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(navController, startDestination = Screen.Main) {
        composable<Screen.Main> {
            OverviewScreen(
                onSettingsClicked = {
                    navController.navigate(Screen.Settings)
                }
            )
        }
        composable<Screen.Settings> { SettingsScreen() }
        composable<Screen.Auth> { SyncScreen() }
    }

}