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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.robingebert.boxy.ui.overview.composables.assets.AssetModal
import com.robingebert.boxy.ui.overview.composables.assets.AssetOption
import com.robingebert.boxy.ui.overview.composables.assets.AssetOptionsBottomSheet
import com.robingebert.boxy.ui.overview.composables.location.LocationGrid
import com.robingebert.boxy.ui.overview.composables.location.LocationModal
import com.robingebert.boxy.ui.overview.composables.location.LocationOption
import com.robingebert.boxy.ui.overview.composables.location.LocationOptionsBottomSheet
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    viewModel: OverviewViewModel = koinViewModel()
) {

    val locations by viewModel.currentLocations.collectAsStateWithLifecycle()
    val breadcrumbs by viewModel.breadcrumbs.collectAsStateWithLifecycle()
    val upNavigationTarget by viewModel.upNavigationTarget.collectAsStateWithLifecycle()
    val assets by viewModel.currentAssets.collectAsStateWithLifecycle()

    var assetDialogState by remember {
        mutableStateOf<EditOptionsDialogState<Asset>>(
            EditOptionsDialogState.None
        )
    }
    var locationDialogState by remember {
        mutableStateOf<EditOptionsDialogState<Location>>(
            EditOptionsDialogState.None
        )
    }

    BackHandler(breadcrumbs.isNotEmpty()) { viewModel.navigateUp() }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FabMenu(
            onNewAsset = {
                viewModel.newAsset()?.let {
                    assetDialogState = EditOptionsDialogState.Edit(it)
                }
            },
            onNewLocation = {
                locationDialogState = EditOptionsDialogState.Edit(viewModel.newLocation())
            },
            showAsset = breadcrumbs.isNotEmpty()
        )
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
                onEditLocation = { locationDialogState = EditOptionsDialogState.Options(it) }
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Assets"
            )
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (val state = assets) {
                    is DataFetcher.Fetching -> item { Text("Loading...") }
                    is DataFetcher.Error -> item { Text("Error") }
                    is DataFetcher.Data -> {
                        items(state.data) { asset ->
                            AssetCard(
                                asset = asset,
                            ) {
                                assetDialogState = EditOptionsDialogState.Options(asset)
                            }
                        }
                    }
                }
            }
        }
    }

    when (val state = assetDialogState) {
        is EditOptionsDialogState.Edit -> {
            AssetModal(
                asset = state.data,
                onDismiss = { assetDialogState = EditOptionsDialogState.None },
                onSave = { updatedAsset ->
                    viewModel.saveAsset(updatedAsset)
                    assetDialogState = EditOptionsDialogState.None
                }
            )
        }

        is EditOptionsDialogState.Options -> {
            AssetOptionsBottomSheet(
                onDismiss = { assetDialogState = EditOptionsDialogState.None }
            ) {
                when (it) {
                    AssetOption.EDIT -> {
                        assetDialogState = EditOptionsDialogState.Edit(state.data)
                    }

                    AssetOption.DELETE -> {
                        viewModel.removeAsset(state.data)
                        assetDialogState = EditOptionsDialogState.None
                    }
                }
            }
        }

        else -> {}
    }

    when (val state = locationDialogState) {
        is EditOptionsDialogState.Edit -> {
            LocationModal(
                location = state.data,
                onDismiss = { locationDialogState = EditOptionsDialogState.None },
                onSave = { updatedLocation ->
                    viewModel.saveLocation(updatedLocation)
                    locationDialogState = EditOptionsDialogState.None
                }
            )
        }

        is EditOptionsDialogState.Options -> {
            LocationOptionsBottomSheet(
                onDismiss = { locationDialogState = EditOptionsDialogState.None }
            ) {
                when (it) {
                    LocationOption.EDIT -> {
                        locationDialogState = EditOptionsDialogState.Edit(state.data)
                    }

                    LocationOption.DELETE -> {
                        viewModel.removeLocation(state.data)
                        locationDialogState = EditOptionsDialogState.None
                    }
                }
            }
        }

        else -> {}
    }
}