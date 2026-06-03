package com.robingebert.boxy.ui.overview.composables.assets

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.domain.models.Asset

@Composable
fun AssetCard(modifier: Modifier = Modifier, asset: Asset, onClick: () -> Unit) {
    Card(
        modifier = modifier.clip(shape = RoundedCornerShape(12.dp)),
        onClick = onClick
    ) {
        Text(modifier = Modifier.padding(8.dp), text = asset.name)
    }
}