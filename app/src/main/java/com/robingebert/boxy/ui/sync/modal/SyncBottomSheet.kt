package com.robingebert.boxy.ui.sync.modal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.ui.main.composables.ServerConnectionDetailsDialog
import com.robingebert.boxy.ui.sync.SyncViewModel
import com.robingebert.boxy.ui.sync.modal.composables.ConnectionCard
import com.robingebert.boxy.ui.sync.modal.composables.DownloadLatestVersion
import com.robingebert.boxy.ui.sync.modal.composables.DownloadedVersion
import com.robingebert.boxy.ui.sync.modal.composables.UploadLocalVersion
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: SyncViewModel = koinViewModel(),
    onDismiss: () -> Unit,
    onShowVersions: () -> Unit
) {

    val url by viewModel.url.collectAsStateWithLifecycle()
    val username by viewModel.username.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val localVersion by viewModel.pulledVersion.collectAsStateWithLifecycle()

    val latest by viewModel.latestVersion.collectAsStateWithLifecycle()

    var showConnectionDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        dragHandle = { Spacer(Modifier.height(4.dp)) }
    ) {

        Column(
            modifier = modifier.padding(12.dp)
        ) {
            ConnectionCard(
                url = url,
                username = username,
            ) {
                showConnectionDialog = true
            }
            if (url.isNotBlank()) {
                if (latest !is DataFetcher.Error) {
                    Spacer(Modifier.height(8.dp))
                    DownloadedVersion(
                        modifier = Modifier
                            .fillMaxWidth(),
                        latestVersion = latest,
                        localVersionString = localVersion,
                        onShowVersions = onShowVersions
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UploadLocalVersion(
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.pushNewVersion()
                        onDismiss()
                    }
                    DownloadLatestVersion(
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.pullLatestVersion()
                        onDismiss()
                    }
                }
            }
        }

        if (showConnectionDialog) {
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
}