package com.robingebert.boxy.ui.main.composables

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.domain.models.Location
import com.robingebert.boxy.ui.common.composables.CoilImage

@Composable
fun LocationCard(
    modifier: Modifier = Modifier,
    location: Location,
    compact: Boolean = true,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth().combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        ),
    ) {
        if (compact) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                
                ImageWithPlaceholder(
                    modifier = Modifier.size(48.dp),
                    imageName = location.picture
                )
                Text(
                    text = location.name,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Column {
                ImageWithPlaceholder(
                    modifier = Modifier.size(120.dp),
                    imageName = location.picture
                )
                Text(
                    text = location.name,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ImageWithPlaceholder(
    modifier: Modifier = Modifier,
    imageName: String?
) {
    if (imageName != null) {
        CoilImage(
            modifier = modifier,
            name = imageName,
        )
    } else {
        Icon(
            modifier = modifier,
            imageVector = Icons.Default.Inventory,
            contentDescription = null,
        )
    }
}

@Preview
@Composable
fun LocationCardPreview() {
    Column {
        LocationCard(
            location = Location(
                name = "Warehouse",
                picture = null,
                id = 1L,
                updated = "",
                parentId = null
            ),
            compact = false,
            onClick = {},
            onLongClick = {}
        )
        LocationCard(
            location = Location(
                name = "Warehouse",
                picture = null,
                id = 1L,
                updated = "",
                parentId = null
            ),
            onClick = {},
            onLongClick = {}
        )
    }
}