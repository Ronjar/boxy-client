package com.robingebert.boxy.ui.overview.composables.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.domain.models.Location
import com.robingebert.boxy.domain.models.LocationNode
import com.robingebert.boxy.ui.common.EditOptionsDialogState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LocationGrid(
    modifier: Modifier = Modifier,
    breadcrumbs: List<Location>,
    locations: DataFetcher<List<LocationNode>>,
    onNavigateUp: () -> Unit,
    onNavigateDown: (Location) -> Unit,
    onSaveLocation: (Location) -> Unit,
    onDeleteLocation: (Location) -> Unit,
    onAddLocation: () -> Location
) {

    var compactLocationCards by rememberSaveable { mutableStateOf(true) }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Breadcrumbs(
                        modifier = Modifier.weight(1f),
                        breadcrumbs = listOf("Startseite") + breadcrumbs.map { it.name }
                    ) { index ->
                        for (i in breadcrumbs.size - index downTo 1) {
                            onNavigateUp()
                        }
                    }
                    val size = ButtonDefaults.ExtraSmallContainerHeight
                    Button(
                        modifier = Modifier.heightIn(size),
                        contentPadding = ButtonDefaults.contentPaddingFor(size, hasStartIcon = true),
                        onClick = {
                            locationDialogState = EditOptionsDialogState.Edit(onAddLocation())
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ort hinzufügen",
                            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
                        )
                        Text("Hinzufügen", style = ButtonDefaults.textStyleFor(size))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    columns = GridCells.Fixed(2)
                ) {
                    when (locations) {
                        is DataFetcher.Fetching -> item { Text("Lädt...") }
                        is DataFetcher.Error -> item { Text("Fehler") }
                        is DataFetcher.Data -> {
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
                        }
                    }
                }
            }
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