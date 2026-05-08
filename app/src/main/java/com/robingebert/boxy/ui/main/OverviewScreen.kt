package com.robingebert.boxy.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.domain.models.Asset
import com.robingebert.boxy.domain.models.Location
import com.robingebert.boxy.ui.common.EditOptionsDialogState
import com.robingebert.boxy.ui.main.composables.AssetCard
import com.robingebert.boxy.ui.main.composables.AssetModal
import com.robingebert.boxy.ui.main.composables.LocationCard
import com.robingebert.boxy.ui.main.composables.FabMenu
import com.robingebert.boxy.ui.main.composables.LocationModal
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    viewModel: OverviewViewModel = koinViewModel(),
    onSettingsClicked: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val locations by viewModel.currentLocations.collectAsStateWithLifecycle()
    val currentParent by viewModel.currentParent.collectAsStateWithLifecycle()
    val assets = viewModel.currentAssets

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


    BackHandler(viewModel.hasParentLocation) { viewModel.navigateUp() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Locations") },
                actions = {
                    IconButton(onClick = {
                        scope.launch { onSettingsClicked() }
                    }) {
                        Icon(
                            Icons.Rounded.Settings,
                            null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FabMenu(
                onNewAsset = {
                    viewModel.newAsset()?.let {
                        assetDialogState = EditOptionsDialogState.Edit(it)
                    }
                },
                onNewLocation = {
                    locationDialogState = EditOptionsDialogState.Edit(viewModel.newLocation())
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Locations"
            )
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth(),
                columns = GridCells.Fixed(3)
            ) {
                when (val state = locations) {
                    is DataFetcher.Fetching -> item { Text("Loading...") }
                    is DataFetcher.Error -> item { Text("Error") }
                    is DataFetcher.Data -> {
                        items(state.data) { location ->
                            currentParent?.let {
                                LocationCard(
                                    modifier = Modifier.padding(8.dp),
                                    location = it,
                                    compact = true,
                                ) {
                                    viewModel.navigateUp()
                                }
                            }
                            LocationCard(
                                modifier = Modifier.padding(8.dp),
                                location = location.location,
                                compact = true,
                                onLongClick = {
                                    locationDialogState =
                                        EditOptionsDialogState.Edit(location.location)
                                }
                            ) {
                                viewModel.changeLocation(location.location)
                            }
                        }
                    }
                }
            }
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Assets"
            )
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                columns = GridCells.Fixed(3)
            ) {
                when (assets) {
                    is DataFetcher.Fetching -> item { Text("Loading...") }
                    is DataFetcher.Error -> item { Text("Error") }
                    is DataFetcher.Data -> {
                        items(assets.data) { asset ->
                            AssetCard(
                                modifier = Modifier.padding(8.dp),
                                asset = asset,
                            ) {
                                assetDialogState = EditOptionsDialogState.Edit(asset)
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

        else -> {}
    }
}