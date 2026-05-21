package com.robingebert.boxy.ui.overview.composables.assets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robingebert.boxy.domain.models.Asset
import com.robingebert.boxy.ui.common.composables.IconRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetModal(asset: Asset, onDismiss: () -> Unit, onSave: (Asset) -> Unit) {
    var name by remember { mutableStateOf(asset.name) }
    fun save() {
        onSave(asset.copy(name = name))
        onDismiss()
    }


    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        dragHandle = {Spacer(Modifier.height(16.dp))}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 20.dp),
        ) {
            IconRow(Icons.Rounded.Title) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("name") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.padding(start = 46.dp)) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { save() }
                ) {
                    Icon(Icons.Rounded.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save")
                }
            }
        }
    }
}