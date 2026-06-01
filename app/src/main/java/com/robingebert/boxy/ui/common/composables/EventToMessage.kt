package com.robingebert.boxy.ui.common.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import com.robingebert.boxy.domain.BackupFailedException
import com.robingebert.boxy.domain.NetworkException
import com.robingebert.boxy.domain.NoBackupsYetException
import com.robingebert.boxy.domain.RestoreFailedException
import com.robingebert.boxy.ui.common.SnackbarEvent
import com.robingebert.boxy.domain.VersionNotFoundException

typealias EventMessageResolver = (SnackbarEvent) -> String


enum class Event {
    BACKUP_SUCCESS,
    RESTORE_SUCCESS,
    DELETE_SUCCESS
}

@Composable
fun rememberEventMessageResolver(): EventMessageResolver {
    val strings = object {
        val noBackupsYet = "No backups yet"
        val versionNotFound = "Version not found"
        val networkError = "Network error"
        val backupFailed = "Backup failed"
        val restoreFailed = "Restore failed"
        val unknownError = "Unknown error"
    }
    return rememberUpdatedState< EventMessageResolver> { e ->
        if (e is SnackbarEvent.SnackbarException) {
            when (e.throwable) {
                is NoBackupsYetException -> strings.noBackupsYet
                is VersionNotFoundException -> strings.versionNotFound
                is NetworkException -> strings.networkError
                is BackupFailedException -> strings.backupFailed
                is RestoreFailedException -> strings.restoreFailed
                else -> strings.unknownError
            }
        }
        else if (e is SnackbarEvent.SnackbarMessage) {
            if (e.message == Event.BACKUP_SUCCESS) {
                "Backup successful"
            } else if (e.message == Event.RESTORE_SUCCESS) {
                "Restore successful"
            } else if (e.message == Event.DELETE_SUCCESS) {
                "Delete successful"
            } else {
                strings.unknownError
            }
        } else {
            strings.unknownError
        }
    }.value
}