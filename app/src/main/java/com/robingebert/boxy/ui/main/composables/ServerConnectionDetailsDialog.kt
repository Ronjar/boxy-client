package com.robingebert.boxy.ui.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ServerConnectionDetailsDialog(
    initialUrl: String = "",
    initialUsername: String = "",
    initialToken: String = "",
    onDismiss: () -> Unit,
    onConnect: (url: String, username: String, token: String) -> Unit
) {
    var url by remember { mutableStateOf(initialUrl) }
    var username by remember { mutableStateOf(initialUsername) }
    var token by remember { mutableStateOf(initialToken) }
    var connectionPossible by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(url) {

    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Server-Verbindung")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Server URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Benutzername") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Token") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConnect(url, username, token) },
                // Button nur aktivieren, wenn alle Felder gefüllt sind
                enabled = url.isNotBlank() && username.isNotBlank() && token.isNotBlank()
            ) {
                Text("Verbinden")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}