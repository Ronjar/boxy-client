package com.robingebert.boxy.ui.overview.composables.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SearchItem(modifier: Modifier = Modifier, text: String, icon: @Composable (Modifier) -> Unit = {}, onClick: () -> Unit) {
    Box(modifier = modifier.clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon(Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}