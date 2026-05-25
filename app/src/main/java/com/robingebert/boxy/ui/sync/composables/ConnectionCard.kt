package com.robingebert.boxy.ui.sync.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.ui.common.composables.shimmerLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun ConnectionCard(
    modifier: Modifier = Modifier,
    url: String,
    username: String,
    onConnectionSettings: () -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surfaceContainer
    var connectionPossible by remember { mutableStateOf<Boolean?>(false) }

    LaunchedEffect(url) {
        connectionPossible = null
        connectionPossible = withContext(Dispatchers.IO) {
            try {
                val url = URL(url)
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 3000
                connection.readTimeout = 3000

                connection.responseCode == 418
            } catch (_: Exception) {
                false
            }
        }
    }

    Card(
        modifier = modifier.shimmerLoading(isLoading = connectionPossible == null),
        colors = CardDefaults.cardColors(
            when (connectionPossible) {
                true -> Color.Green
                false -> Color.Red
                null -> surfaceColor
            }.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                val text = when (connectionPossible) {
                    true -> "Connected"
                    false -> "Connection not possible"
                    null -> "Checking connection..."
                }
                if (url.isBlank()) {
                    Text(text = "No server configured")
                } else {
                    Text(text = text)
                    Text(text = url, style = MaterialTheme.typography.labelSmall)
                }
            }
            FilledTonalIconButton(
                onClick = onConnectionSettings,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.2f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Connection Settings"
                )
            }
        }
    }
}

@Preview
@Composable
fun ConnectionCardPreview() {
    ConnectionCard(
        url = "http://example.com",
        username = "user",
        onConnectionSettings = {}
    )
}