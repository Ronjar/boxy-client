package com.robingebert.boxy.ui.main.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.robingebert.boxy.ui.navigation.Screen
import com.robingebert.boxy.ui.sync.modal.SyncBottomSheet

@Composable
fun MainLayout(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    hasLocalChanges: Boolean,
    content: @Composable (() -> Unit)
) {
    var showSyncBottomSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Locations") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Settings)

                    }) {
                        Icon(
                            Icons.Rounded.Settings,
                            null
                        )
                    }
                    IconButton(onClick = {
                        showSyncBottomSheet = true
                    }) {
                        BadgedBox(
                            badge = {
                                if (hasLocalChanges) {
                                    Badge {
                                        Text(text = "!")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Rounded.Sync,
                                null
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }

    if (showSyncBottomSheet) {
        SyncBottomSheet(
            onDismiss = {
                showSyncBottomSheet = false
            }
        ) {
            showSyncBottomSheet = false
            navController.navigate(Screen.Sync)
        }
    }
}