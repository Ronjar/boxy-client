package com.robingebert.boxy.ui.common.composables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import kotlin.time.Duration

@Composable
fun TimeUnitAgoText(modifier: Modifier = Modifier, textStyle: TextStyle, ago: Duration) {
    val text = when {
        ago.inWholeSeconds < 60 -> "Just now"
        ago.inWholeMinutes < 60 -> "${ago.inWholeMinutes} minute${if (ago.inWholeMinutes > 1) "s" else ""} ago"
        ago.inWholeHours < 24 -> "${ago.inWholeHours} hour${if (ago.inWholeHours > 1) "s" else ""} ago"
        else -> "${ago.inWholeDays} day${if (ago.inWholeDays > 1) "s" else ""} ago"
    }


    Text(text = text, modifier = modifier)
}