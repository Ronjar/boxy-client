package com.robingebert.boxy.ui.sync.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.domain.VersionInfo
import com.robingebert.boxy.ui.sync.SyncViewModel
import com.robingebert.boxy.ui.sync.screen.composables.DeleteModal
import com.robingebert.boxy.ui.sync.screen.composables.VersionItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun SyncScreen(viewModel: SyncViewModel = koinViewModel()) {
    val versions by viewModel.versions.collectAsStateWithLifecycle()
    var showDeleteFor: VersionInfo? by remember { mutableStateOf(null) }

    when(val state = versions) {
        is DataFetcher.Fetching -> {
            // Show loading state
        }
        is DataFetcher.Error -> {
            // Show error state
        }
        is DataFetcher.Data -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.data) { version ->
                    VersionItem(
                        version = version,
                        onDownload = { viewModel.pullVersion(version.id) },
                        onDelete = {
                            showDeleteFor = version
                        }
                    )
                }
            }
        }
    }

    showDeleteFor?.let {
        DeleteModal(
            onDismiss = { showDeleteFor = null },
            onDelete = {
                viewModel.deleteVersion(it)
                showDeleteFor = null
            }
        )
    }
}