package com.robingebert.boxy.ui.overview.composables.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Blender
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.ui.common.composables.ImageWithPlaceholder
import com.robingebert.boxy.ui.overview.SearchResult

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchModal(
    onDismiss: () -> Unit,
    searchResults: DataFetcher<SearchResult>?,
    onSearch: (String, Boolean) -> Unit,
    onResultClicked: (Long?) -> Unit
) {
    var query by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { Spacer(Modifier.height(2.dp)) },
        sheetState = rememberBottomSheetState(
            initialValue = SheetValue.Hidden,
            enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            SimpleCustomSearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = onSearch,
                onDismiss = onDismiss
            )
            Spacer(Modifier.height(12.dp))
            searchResults?.let {
                if (it is DataFetcher.Fetching) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator(modifier = Modifier.size(96.dp))
                    }
                } else if (it is DataFetcher.Data) {
                    val results = it.data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(results.locations) { location ->
                            SearchItem(
                                modifier = Modifier.fillMaxWidth(),
                                text = location.name,
                                icon = { modifier ->
                                    ImageWithPlaceholder(
                                        modifier = modifier,
                                        imageName = location.picture
                                    )
                                }
                            ) {
                                onResultClicked(location.id)
                            }
                        }
                        items(results.assets) { asset ->
                            SearchItem(
                                modifier = Modifier.fillMaxWidth(),
                                text = asset.name,
                                icon = { modifier ->
                                    Icon(
                                        modifier = modifier,
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
}