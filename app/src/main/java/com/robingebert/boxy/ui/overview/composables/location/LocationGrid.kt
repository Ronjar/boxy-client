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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Breadcrumbs(
                        modifier = Modifier.weight(1f),
                        breadcrumbs = listOf("/") + breadcrumbs.map { it.name }
                    ) { index ->
                        for (i in breadcrumbs.size - index downTo 1) {
                            onNavigateUp()
                        }
                    }
                    val size = 36.dp
                    FilledIconButton(
                        modifier = Modifier.size(size),
                        onClick = {
                            locationDialogState = EditOptionsDialogState.Edit(onAddLocation())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ort hinzufügen",
                            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
                        )
                    }
                    OutlinedIconButton(
                        modifier = Modifier.size(size),
                        onClick = {
                            compactLocationCards = !compactLocationCards
                        }
                    ) {
                        val icon = if (compactLocationCards) {
                            Icons.Default.ViewHeadline
                        } else {
                            Icons.Default.ViewColumn
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = "Ansicht wechseln",
                            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                when (locations) {
                    is DataFetcher.Fetching -> Text("Lädt...")
                    is DataFetcher.Error -> Text("Fehler")
                    is DataFetcher.Data -> {

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val rows = locations.data.chunked(if (compactLocationCards)2 else 3)
                            rows.forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowItems.forEach { location ->
                                        LocationCard(
                                            modifier = Modifier
                                                .weight(1f)
                                                .alpha(1f),
                                            location = location.location,
                                            compact = compactLocationCards,
                                            onLongClick = {
                                                locationDialogState =
                                                    EditOptionsDialogState.Options(location.location)
                                            }
                                        ) {
                                            onNavigateDown(location.location)
                                        }
                                    }
                                    if (rowItems.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                    if (rowItems.size <= 2 && !compactLocationCards) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
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