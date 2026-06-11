package com.robingebert.boxy.ui.overview.composables.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.robingebert.boxy.domain.models.Location
import com.robingebert.boxy.ui.common.composables.ImageWithPlaceholder

@Composable
fun PathLocationCard(location: Location, isTarget: Boolean, modifier: Modifier = Modifier) {

    val cardColors = if (isTarget) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    }


    Card(
        modifier = modifier,
        colors = cardColors
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageWithPlaceholder(
                modifier = Modifier.size(60.dp),
                imageName = location.picture
            )
            Text(
                text = location.name,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun PathDialog(path: List<Location>, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(path.reversed()) { location ->
                    PathLocationCard(location = location, modifier = Modifier.fillMaxWidth(), isTarget = location == path.last())
                    if (location != path.first()) {
                        Icon(
                            modifier = Modifier.padding(vertical = 8.dp),
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}