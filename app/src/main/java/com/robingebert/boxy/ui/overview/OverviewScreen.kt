package com.robingebert.boxy.ui.overview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robingebert.boxy.ui.overview.composables.assets.AssetGrid
import com.robingebert.boxy.ui.overview.composables.location.LocationGrid
import com.robingebert.boxy.ui.overview.composables.location.PathDialog
import com.robingebert.boxy.ui.overview.composables.search.SearchModal
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    viewModel: OverviewViewModel = koinViewModel()
) {

    val locations by viewModel.currentLocations.collectAsStateWithLifecycle()
    val assets by viewModel.currentAssets.collectAsStateWithLifecycle()
    val breadcrumbs by viewModel.breadcrumbs.collectAsStateWithLifecycle()

    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    var showSearchDialog by remember { mutableStateOf(false) }

    BackHandler(breadcrumbs.isNotEmpty() && !showSearchDialog) { viewModel.navigateUp() }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            LocationGrid(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                breadcrumbs = breadcrumbs,
                locations = locations,
                onNavigateUp = { viewModel.navigateUp() },
                onNavigateDown = { viewModel.navigateDown(it) },
                onAddLocation = { viewModel.newLocation() },
                onSaveLocation = { viewModel.saveLocation(it) },
                onDeleteLocation = { viewModel.removeLocation(it) }
            )
            AssetGrid(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                title = breadcrumbs.lastOrNull()?.name?: "Home",
                assets = assets,
                onNewAsset = { viewModel.newAsset() },
                onSaveAsset = { viewModel.saveAsset(it) },
                onDeleteAsset = { viewModel.removeAsset(it) }
            )
        }

        FloatingActionButton(
            modifier = Modifier.padding(24.dp),
            onClick = {
                showSearchDialog = true
            }
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        }
    }

    if (showSearchDialog) {
        SearchModal(
            onDismiss = {
                showSearchDialog = false
                viewModel.clearSearch()
            },
            searchResults = searchResults,
            onSearch = { query, useAiSearch -> viewModel.search(query, useAiSearch) },
        ) {
            showSearchDialog = false
            viewModel.clearSearch()
            viewModel.navigateTo(it)
        }
    }
}