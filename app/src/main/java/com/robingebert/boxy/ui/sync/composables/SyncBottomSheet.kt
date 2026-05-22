package com.robingebert.boxy.ui.sync.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robingebert.boxy.ui.main.composables.ServerConnectionDetailsDialog
import com.robingebert.boxy.ui.sync.SyncViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SyncBottomSheet(modifier: Modifier = Modifier, viewModel: SyncViewModel = koinViewModel()) {

    val url by viewModel.url.collectAsStateWithLifecycle()
    val username by viewModel.username.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()

    var showConnectionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(8.dp)
    ) {
        ConnectionCard(
            url = url,
            username = username,
        ) {
            showConnectionDialog = true
        }
    }

    if (showConnectionDialog){
        ServerConnectionDetailsDialog(
            initialUrl = url,
            initialUsername = username,
            initialToken = password,
            onDismiss = {
                showConnectionDialog = false
            },
            onConnect = { finalUrl, finalUsername, finalToken ->
                viewModel.setCredentials(finalUrl, finalUsername, finalToken)
                showConnectionDialog = false
            }
        )
    }
}