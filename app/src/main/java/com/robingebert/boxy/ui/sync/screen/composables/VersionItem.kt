package com.robingebert.boxy.ui.sync.screen.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.domain.VersionInfo
import com.robingebert.boxy.ui.common.composables.dateTimeFormatter
import com.robingebert.boxy.ui.common.composables.timeUnitAgo

@Composable
fun VersionItem(
    modifier: Modifier = Modifier,
    version: VersionInfo,
    onDownload: () -> Unit = {},
    onDelete: () -> Unit = {}
) {

    val dateText = "${dateTimeFormatter(version.date)} (${timeUnitAgo(version.getSince())})"
    Card(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = dateText, style = MaterialTheme.typography.titleMedium)
                Text(text = "von ${version.user}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(
                onClick = onDownload
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )
            }
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}