package com.robingebert.boxy.ui.overview.composables.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.domain.models.Location
import com.robingebert.boxy.domain.models.LocationNode
import com.robingebert.boxy.ui.common.EditOptionsDialogState
import com.robingebert.boxy.ui.overview.UpNavigationTarget

@Composable
fun LocationGrid(
    modifier: Modifier = Modifier,
    breadcrumbs: List<Location>,
    upNavigationTarget: UpNavigationTarget?,
    locations: DataFetcher<List<LocationNode>>,
    onNavigateUp: () -> Unit,
    onNavigateDown: (Location) -> Unit,
    onSaveLocation: (Location) -> Unit,
    onDeleteLocation: (Location) -> Unit,
    onAddLocation: () -> Location
) {

    var compactLocationCards by rememberSaveable { mutableStateOf(true) }
    val breadcrumbText = breadcrumbs.joinToString(separator = "") { "${it.name} / " }

    var locationDialogState by remember {
        mutableStateOf<EditOptionsDialogState<Location>>(
            EditOptionsDialogState.None
        )
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {

        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .alpha(0.8f),
                    text = "/ $breadcrumbText",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    columns = GridCells.Fixed(2)
                ) {
                    when (locations) {
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
                                            onNavigateUp()
                                        }
                                    }
                                } else {
                                    item {
                                        HomeCard(
                                            modifier = Modifier.alpha(0.6f),
                                            compact = compactLocationCards,
                                        ) {
                                            onNavigateUp()
                                        }
                                    }
                                }
                            }
                            items(locations.data) { location ->
                                LocationCard(
                                    modifier = Modifier.alpha(1f),
                                    location = location.location,
                                    compact = compactLocationCards,
                                    onLongClick = {
                                        locationDialogState = EditOptionsDialogState.Options(location.location)
                                    }
                                ) {
                                    onNavigateDown(location.location)
                                }
                            }
                            item {
                                AddLocationCard(
                                    modifier = Modifier.alpha(0.8f)
                                ) {
                                    locationDialogState = EditOptionsDialogState.Edit(onAddLocation())
                                }
                            }
                        }
                    }
                }
            }/*
            Spacer(modifier = Modifier.width(8.dp))
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
            }*/
        }

    }

    when (val state = locationDialogState) {
        is EditOptionsDialogState.Edit -> {
            LocationModal(
                location = state.data,
                onDismiss = { locationDialogState = EditOptionsDialogState.None },
                onSave = { updatedLocation ->
                    onSaveLocation(updatedLocation)
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
                        onDeleteLocation(state.data)
                        locationDialogState = EditOptionsDialogState.None
                    }
                }
            }
        }

        else -> {}
    }
}