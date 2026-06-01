package com.robingebert.boxy.ui.overview.composables.assets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.domain.models.Asset
import com.robingebert.boxy.ui.common.EditOptionsDialogState

@Composable
fun AssetGrid(modifier: Modifier = Modifier, assets: DataFetcher<List<Asset>>, onNewAsset: () -> Asset, onSaveAsset: (Asset) -> Unit, onDeleteAsset: (Asset) -> Unit) {
    var assetDialogState by remember {
        mutableStateOf<EditOptionsDialogState<Asset>>(
            EditOptionsDialogState.None
        )
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(1),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (assets) {
            is DataFetcher.Fetching -> item { Text("Loading...") }
            is DataFetcher.Error -> item { Text("Error") }
            is DataFetcher.Data -> {
                items(assets.data) { asset ->
                    AssetCard(
                        asset = asset,
                    ) {
                        assetDialogState = EditOptionsDialogState.Options(asset)
                    }
                }
                item {
                    AddAssetCard {
                        assetDialogState = EditOptionsDialogState.Edit(onNewAsset())
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
                    onSaveAsset(updatedAsset)
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
                        onDeleteAsset(state.data)
                        assetDialogState = EditOptionsDialogState.None
                    }
                }
            }
        }

        else -> {}
    }
}