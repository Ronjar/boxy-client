package com.robingebert.boxy.ui.overview.composables.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Blender
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.ui.common.composables.ImageWithPlaceholder
import com.robingebert.boxy.ui.overview.SearchResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchModal(
    onDismiss: () -> Unit,
    searchResults: SearchResult?,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    onResultClicked: (Long?) -> Unit
) {
    var query by remember { mutableStateOf("") }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { Spacer(Modifier.height(2.dp)) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            SimpleCustomSearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = { onSearch(query) },
                onClearSearch = {
                    query = ""
                    onClearSearch()
                },
                onDismiss = onDismiss
            )
            Spacer(Modifier.height(12.dp))
            searchResults?.let {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    columns = GridCells.Fixed(3)
                ) {
                    items(it.locations) { location ->
                        SearchItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = location.name,
                            icon = {
                                ImageWithPlaceholder(
                                    modifier = Modifier.size(40.dp).padding(4.dp),
                                    imageName = location.picture
                                )
                            }
                        ) {
                            onResultClicked(location.id)
                        }
                    }
                    items(it.assets, span = { GridItemSpan(3) }) { asset ->
                        SearchItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = asset.name,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Blender,
                                    contentDescription = null
                                )
                            }
                        ) {
                            onResultClicked(asset.parentId)
                        }
                    }
                }
            }
        }
    }
}