package com.robingebert.boxy.ui.overview.composables.location

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class LocationOption {
    EDIT,
    DELETE,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationOptionsBottomSheet(
    onDismiss: () -> Unit,
    onSelected: (LocationOption) -> Unit
) {
    ModalBottomSheet(
        content = {
            ListItem(
                icon = Icons.Default.Edit,
                title = "Edit"
            ) {
                onSelected(LocationOption.EDIT)
            }
            ListItem(
                icon = Icons.Default.DeleteForever,
                title = "Delete"
            ) {
                onSelected(LocationOption.DELETE)
            }

        },
        dragHandle = { Spacer(Modifier.height(16.dp))},
        onDismissRequest = { onDismiss() }
    )
}

@Composable
fun ListItem(modifier: Modifier = Modifier, icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .padding(horizontal = 15.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onClick()
            }
    ) {
        Row(
            Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(modifier = Modifier.size(30.dp), imageVector = icon, contentDescription = "")
            Spacer(Modifier.width(10.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}