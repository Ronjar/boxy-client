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
                is NoBackupsYetException -> EventMessage("Noch keine Backups vorhanden", null)
                is VersionNotFoundException -> EventMessage("Version nicht gefunden", null)
                is NetworkException -> EventMessage("Netzwerkfehler", null)
                is BackupFailedException -> EventMessage("Backup fehlgeschlagen", null)
                is RestoreFailedException -> EventMessage("Wiederherstellung fehlgeschlagen", null)
                else -> EventMessage("Unbekannter Fehler", null)
            }
        }
        else if (e is SnackbarEvent.SnackbarMessage) {
            when(e.message) {
                is Event.BackupSuccess -> EventMessage("Backup erfolgreich", null)
                is Event.RestoreSuccess -> EventMessage("Wiederherstellung erfolgreich", null)
                is Event.DeleteSuccess -> EventMessage("Löschen erfolgreich", null)
                is Event.UpdateAvailable -> EventMessage("Update verfügbar", "Aktualisieren") {
                    e.message.onClick()
                }
                is Event.UpdateInstallable -> EventMessage("Update kann installiert werden", "Installieren") {
                    e.message.onClick()
                }
            }
        } else {
            EventMessage("Unbekannter Fehler", null)
        }
    }.value
}