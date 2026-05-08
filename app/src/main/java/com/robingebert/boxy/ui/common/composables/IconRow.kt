package com.robingebert.boxy.ui.common.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun IconRow(
    icon: ImageVector,
    content: @Composable () -> Unit,
) {
    Row {
        Icon(modifier = Modifier.size(30.dp), imageVector = icon, contentDescription = "")
        Spacer(modifier = Modifier.width(16.dp))
        content()
    }
}