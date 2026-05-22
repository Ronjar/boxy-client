package com.robingebert.boxy.ui.overview.composables.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.domain.models.Location
import com.robingebert.boxy.domain.models.LocationNode
import com.robingebert.boxy.ui.overview.UpNavigationTarget

@Composable
fun LocationGrid(
    modifier: Modifier = Modifier,
    breadcrumbs: List<Location>,
    upNavigationTarget: UpNavigationTarget?,
    locations: DataFetcher<List<LocationNode>>,
    onNavigateUp: () -> Unit,
    onNavigateDown: (Location) -> Unit,
    onEditLocation: (Location) -> Unit,
) {

    var compactLocationCards by rememberSaveable { mutableStateOf(true) }

    val breadcrumbText = breadcrumbs.joinToString(separator = "") { "${it.name} / " }

    Column(
        modifier = modifier
    ) {
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
                                    onEditLocation(location.location)
                                }
                            ) {
                                onNavigateDown(location.location)
                            }
                        }
                    }
                }
            }
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
            }
        }
    }
}