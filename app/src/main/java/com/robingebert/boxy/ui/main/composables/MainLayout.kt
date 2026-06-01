package com.robingebert.boxy.ui.main.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.navigation.NavHostController
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.ui.common.SnackbarController
import com.robingebert.boxy.ui.common.composables.rememberEventMessageResolver
import com.robingebert.boxy.ui.navigation.Destination
import com.robingebert.boxy.ui.navigation.Screen
import com.robingebert.boxy.ui.sync.modal.SyncBottomSheet
import org.koin.compose.koinInject

@Composable
fun MainLayout(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    hasLocalChanges: Boolean,
    hasRemoteChanges: DataFetcher<Boolean>,
    destination: Destination,
    content: @Composable (() -> Unit)
) {
    var showSyncBottomSheet by remember { mutableStateOf(false) }

    val syncRotation = if (hasRemoteChanges is DataFetcher.Fetching) {
        val transition = rememberInfiniteTransition(label = "syncRotation")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = -360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "syncRotation"
        ).value
    } else {
        0f
    }

    val eventToMessage = rememberEventMessageResolver()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarController: SnackbarController = koinInject()

    LaunchedEffect(snackbarController, snackbarHostState) {
        snackbarController.events.collect { event ->
            snackbarHostState.showSnackbar(
                message = eventToMessage(event),
                duration = SnackbarDuration.Short
            )
        }
    }


    val hasChanges = hasLocalChanges || hasRemoteChanges.dataOrNull() == true

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(destination.name) },
                actions = {
                    if (destination.isMain) {
                        IconButton(onClick = {
                            showSyncBottomSheet = true
                        }) {
                            BadgedBox(
                                badge = {
                                    if (hasChanges) {
                                        Badge {
                                            Text(text = "!")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Sync,
                                    contentDescription = null,
                                    modifier = Modifier.rotate(syncRotation)
                                )
                            }
                        }
                        IconButton(onClick = {
                            navController.navigate(Screen.Settings)

                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = null
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (!destination.isMain) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        },
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