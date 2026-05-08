package com.robingebert.boxy.ui.main.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.domain.models.Asset

@Composable
fun AssetCard(modifier: Modifier, asset: Asset, onClick: () -> Unit) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Text(modifier = Modifier.padding(8.dp), text = asset.name)
    }
}