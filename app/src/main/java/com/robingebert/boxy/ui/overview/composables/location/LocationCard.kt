package com.robingebert.boxy.ui.overview.composables.location

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.domain.models.Location
import com.robingebert.boxy.ui.common.composables.ImageWithPlaceholder

@Composable
fun LocationCard(
    modifier: Modifier = Modifier,
    location: Location,
    compact: Boolean = true,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(shape = RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (compact) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageWithPlaceholder(
                    modifier = Modifier.size(40.dp).padding(4.dp),
                    imageName = location.picture
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = location.name,
                    modifier = Modifier.weight(1f),
                    minLines = 1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            Column {
                ImageWithPlaceholder(
                    modifier = Modifier.size(120.dp),
                    imageName = location.picture
                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = location.name,
                    style = MaterialTheme.typography.labelSmall,
                    minLines = 3,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun LocationCardPreview() {
    Column {
        LocationCard(
            location = Location(
                name = "Lagerhaus",
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
                name = "Lagerhaus",
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