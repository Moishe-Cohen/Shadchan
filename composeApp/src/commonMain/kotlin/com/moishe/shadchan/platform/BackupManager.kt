package com.moishe.shadchan.platform

/**
 * Backup = a single ZIP containing the SQLite database plus the photos/ and resumes/ folders.
 * Restore replaces all three and resets the DB connection; the app should be restarted
 * afterwards so the driver reopens the restored file cleanly.
 */
expect class BackupManager(context: PlatformContext) {
    fun createBackup(destination: String): Result<Unit>
    fun restoreBackup(source: String): Result<Unit>
}
