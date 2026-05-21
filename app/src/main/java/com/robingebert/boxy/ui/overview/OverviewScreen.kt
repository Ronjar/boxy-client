package com.robingebert.boxy.ui.overview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.robingebert.boxy.ui.overview.composables.location.HomeCard
import com.robingebert.boxy.ui.overview.composables.location.LocationCard
import com.robingebert.boxy.ui.overview.composables.location.LocationModal
import com.robingebert.boxy.ui.overview.composables.location.LocationOption
import com.robingebert.boxy.ui.overview.composables.location.LocationOptionsBottomSheet
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
    val breadcrumbs by viewModel.breadcrumbs.collectAsStateWithLifecycle()
    val upNavigationTarget by viewModel.upNavigationTarget.collectAsStateWithLifecycle()
    val assets by viewModel.currentAssets.collectAsStateWithLifecycle()

    var compactLocationCards by rememberSaveable { mutableStateOf(true) }

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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

                val breadcrumbText = breadcrumbs.joinToString(separator = "") { "${it.name} / " }
                Text(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .alpha(0.8f),
                    text = "Home / $breadcrumbText",
                    style = MaterialTheme.typography.labelMedium
                )
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        columns = GridCells.Fixed(3)
                    ) {
                        when (val state = locations) {
                            is DataFetcher.Fetching -> item { Text("Loading...") }
                            is DataFetcher.Error -> item { Text("Error") }
                            is DataFetcher.Data -> {
                                upNavigationTarget?.let {
                                    if (it is UpNavigationTarget.Folder) {
                                        item {
                                            LocationCard(
                                                modifier = Modifier.alpha(0.8f),
                                                location = it.location,
                                                compact = compactLocationCards,
                                            ) {
                                                viewModel.navigateUp()
                                            }
                                        }
                                    } else {
                                        item {
                                            HomeCard(
                                                modifier = Modifier.alpha(0.6f),
                                                compact = compactLocationCards,
                                            ) {
                                                viewModel.navigateUp()
                                            }
                                        }
                                    }
                                }
                                items(state.data) { location ->
                                    LocationCard(
                                        modifier = Modifier.alpha(1f),
                                        location = location.location,
                                        compact = compactLocationCards,
                                        onLongClick = {
                                            locationDialogState =
                                                EditOptionsDialogState.Options(location.location)
                                        }
                                    ) {
                                        viewModel.navigateDown(location.location)
                                    }
                                }
                            }
                        }
                    }
                    OutlinedIconButton(
                        onClick = {
                            compactLocationCards = !compactLocationCards
                        }
                    ) {
                        val icon = if (compactLocationCards) {
                            Icons.Default.ViewHeadline
                        } else {
                            Icons.Default.ViewColumn
                        }
                        Icon(imageVector = icon, contentDescription = "Toggle location card size")
                    }
                }
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