package com.robingebert.boxy.ui.common.composables

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Composable
fun dateTimeFormatter(instant: Instant): String {
    val dateTimeFormatter = LocalDateTime.Format {
        day()
        chars(".")
        monthNumber()
        chars(".")
        year()
        chars(" ")
        hour()
        chars(":")
        minute()
        chars(":")
        second()
    }

    return dateTimeFormatter.format(instant.toLocalDateTime(TimeZone.currentSystemDefault()))
}