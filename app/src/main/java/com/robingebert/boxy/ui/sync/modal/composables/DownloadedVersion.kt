package com.robingebert.boxy.ui.sync.modal.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.data.network.DataFetcher
import com.robingebert.boxy.domain.VersionInfo
import com.robingebert.boxy.ui.common.composables.shimmerLoading
import com.robingebert.boxy.ui.common.composables.timeUnitAgo

@Composable
fun DownloadedVersion(
    modifier: Modifier = Modifier,
    latestVersion: DataFetcher<VersionInfo>,
    localVersionString: String,
    onShowVersions: () -> Unit
) {
    Card(
        modifier = modifier.clickable{
            onShowVersions()
        }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (latestVersion is DataFetcher.Data) {
                    val text = when (latestVersion.data.id != localVersionString) {
                        true -> "Neues Update verfügbar"
                        false -> "Neueste Version"
                    }
                    Text(
                        text = text
                    )
                } else {
                    Text(
                        modifier = Modifier.shimmerLoading(isLoading = true),
                        text = "                    ",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(Modifier.height(8.dp))
                if (latestVersion is DataFetcher.Data) {
                    Text(
                        style = MaterialTheme.typography.labelSmall,
                        text = timeUnitAgo(latestVersion.data.getSince())
                    )
                    Text(
                        modifier = Modifier.alpha(0.8f),
                        text = "von ${latestVersion.data.user}",
                        style = MaterialTheme.typography.labelSmall
                    )
                } else {
                    Text(
                        modifier = Modifier.shimmerLoading(isLoading = true),
                        text = "                        ",
                    )
                }
            }
            FilledTonalIconButton(
                onClick = onShowVersions,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.2f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Andere Versionen anzeigen"
                )
            }
        }
    }
}