package com.robingebert.boxy.domain

class NoBackupsYetException : Exception()
class VersionNotFoundException : Exception()
data class NetworkException(val e: Throwable) : Exception(e)
data class BackupFailedException(val e: Throwable) : Exception(e)
data class RestoreFailedException(val e: Throwable) : Exception(e)