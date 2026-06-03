package com.robingebert.boxy.ui.common.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import com.robingebert.boxy.domain.BackupFailedException
import com.robingebert.boxy.domain.NetworkException
import com.robingebert.boxy.domain.NoBackupsYetException
import com.robingebert.boxy.domain.RestoreFailedException
import com.robingebert.boxy.ui.common.SnackbarEvent
import com.robingebert.boxy.domain.VersionNotFoundException
import com.robingebert.boxy.ui.common.Event

data class EventMessage(
    val message: String,
    val actionLabel: String? = null,
    val action: (() -> Unit)? = null
)

typealias EventMessageResolver = (SnackbarEvent) -> EventMessage

@Composable
fun rememberEventMessageResolver(): EventMessageResolver {
    return rememberUpdatedState< EventMessageResolver> { e ->
        if (e is SnackbarEvent.SnackbarException) {
            when (e.throwable) {
                is NoBackupsYetException -> EventMessage("No backups yet", null)
                is VersionNotFoundException -> EventMessage("Version not found", null)
                is NetworkException -> EventMessage("Network error", null)
                is BackupFailedException -> EventMessage("Backup failed", null)
                is RestoreFailedException -> EventMessage("Restore failed", null)
                else -> EventMessage("Unknown error", null)
            }
        }
        else if (e is SnackbarEvent.SnackbarMessage) {
            when(e.message) {
                is Event.BackupSuccess -> EventMessage("Backup successful", null)
                is Event.RestoreSuccess -> EventMessage("Restore successful", null)
                is Event.DeleteSuccess -> EventMessage("Delete successful", null)
                is Event.UpdateAvailable -> EventMessage("Update available", "Update") {
                    e.message.onClick()
                }
                is Event.UpdateInstallable -> EventMessage("Update ready to install", "Install") {
                    e.message.onClick()
                }
            }
        } else {
            EventMessage("Unknown error", null)
        }
    }.value
}