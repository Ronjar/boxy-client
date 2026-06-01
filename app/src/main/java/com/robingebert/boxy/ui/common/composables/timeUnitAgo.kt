package com.robingebert.boxy.ui.common.composables

import androidx.compose.runtime.Composable
import kotlin.time.Duration

@Composable
fun timeUnitAgo(ago: Duration): String {
    val text = when {
        ago.inWholeSeconds < 60 -> "Just now"
        ago.inWholeMinutes < 60 -> "${ago.inWholeMinutes} minute${if (ago.inWholeMinutes > 1) "s" else ""} ago"
        ago.inWholeHours < 24 -> "${ago.inWholeHours} hour${if (ago.inWholeHours > 1) "s" else ""} ago"
        else -> "${ago.inWholeDays} day${if (ago.inWholeDays > 1) "s" else ""} ago"
    }
    return text
}