package com.robingebert.boxy.ui.overview.composables.location

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeCard(
    modifier: Modifier = Modifier,
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
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).padding(4.dp)
                )
                Text(
                    text = "Home",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Column {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(104.dp).padding(8.dp)
                )
                Text(
                    text = "Home",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeCardPreview() {
    Column {
        HomeCard(
            compact = false,
            onClick = {},
            onLongClick = {}
        )
        HomeCard(
            onClick = {},
            onLongClick = {}
        )
    }
}