package com.robingebert.boxy.ui.common.composables

import androidx.compose.runtime.Composable
import kotlin.time.Duration

@Composable
fun timeUnitAgo(ago: Duration): String {
    val text = when {
        ago.inWholeSeconds < 60 -> "Gerade eben"
        ago.inWholeMinutes < 60 -> "${ago.inWholeMinutes} Minute${if (ago.inWholeMinutes > 1) "n" else ""} her"
        ago.inWholeHours < 24 -> "${ago.inWholeHours} Stunde${if (ago.inWholeHours > 1) "n" else ""} her"
        else -> "${ago.inWholeDays} Tag${if (ago.inWholeDays > 1) "e" else ""} her"
    }
    return text
}