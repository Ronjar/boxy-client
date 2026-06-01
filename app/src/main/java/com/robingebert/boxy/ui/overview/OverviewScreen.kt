package com.robingebert.boxy.ui.overview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.domain.models.Asset
import com.robingebert.boxy.domain.models.Location
import com.robingebert.boxy.ui.common.EditOptionsDialogState
import com.robingebert.boxy.ui.overview.composables.FabMenu
import com.robingebert.boxy.ui.overview.composables.assets.AssetCard
import com.robingebert.boxy.ui.overview.composables.assets.AssetGrid
import com.robingebert.boxy.ui.overview.composables.assets.AssetModal
import com.robingebert.boxy.ui.overview.composables.assets.AssetOption
import com.robingebert.boxy.ui.overview.composables.assets.AssetOptionsBottomSheet
import com.robingebert.boxy.ui.overview.composables.location.LocationGrid
import com.robingebert.boxy.ui.overview.composables.location.LocationModal
import com.robingebert.boxy.ui.overview.composables.location.LocationOption
import com.robingebert.boxy.ui.overview.composables.location.LocationOptionsBottomSheet
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
    val upNavigationTarget by viewModel.upNavigationTarget.collectAsStateWithLifecycle()

    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    var showSearchDialog by remember { mutableStateOf(false) }

    BackHandler(breadcrumbs.isNotEmpty() && !showSearchDialog) { viewModel.navigateUp() }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            modifier = Modifier.padding(16.dp),
            onClick = {
                showSearchDialog = true
            }
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LocationGrid(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                breadcrumbs = breadcrumbs,
                upNavigationTarget = upNavigationTarget,
                locations = locations,
                onNavigateUp = { viewModel.navigateUp() },
                onNavigateDown = { viewModel.navigateDown(it) },
                onAddLocation = { viewModel.newLocation() },
                onSaveLocation = { viewModel.saveLocation(it) },
                onDeleteLocation = { viewModel.removeLocation(it) }
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Assets"
            )
            AssetGrid(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                assets = assets,
                onNewAsset = { viewModel.newAsset() },
                onSaveAsset = { viewModel.saveAsset(it) },
                onDeleteAsset = { viewModel.removeAsset(it) }
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
            onSearch = { viewModel.search(it) },
            onClearSearch = { viewModel.clearSearch() },
        ) {
            showSearchDialog = false
            viewModel.clearSearch()
            viewModel.navigateTo(it)
        }
    }
}