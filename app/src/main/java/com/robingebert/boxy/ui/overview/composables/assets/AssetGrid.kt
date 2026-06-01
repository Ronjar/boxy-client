package com.robingebert.boxy.ui.overview.composables.assets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.domain.models.Asset
import com.robingebert.boxy.ui.common.EditOptionsDialogState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AssetGrid(modifier: Modifier = Modifier, assets: DataFetcher<List<Asset>>, onNewAsset: () -> Asset, onSaveAsset: (Asset) -> Unit, onDeleteAsset: (Asset) -> Unit) {
    var assetDialogState by remember {
        mutableStateOf<EditOptionsDialogState<Asset>>(
            EditOptionsDialogState.None
        )
    }

    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Assets"
            )

            val size = ButtonDefaults.ExtraSmallContainerHeight
            Button(
                modifier = Modifier.heightIn(size),
                contentPadding = ButtonDefaults.contentPaddingFor(size, hasStartIcon = true),
                onClick = {
                    assetDialogState = EditOptionsDialogState.Edit(onNewAsset())
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add location",
                    modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
                )
                Text("Add", style = ButtonDefaults.textStyleFor(size))
            }
        }

        LazyVerticalGrid(
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