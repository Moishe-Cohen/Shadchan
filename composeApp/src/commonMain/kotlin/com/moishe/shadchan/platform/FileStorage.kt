package com.moishe.shadchan.platform

/**
 * Copies a user-picked file into app-private storage and returns the resulting absolute path
 * (only the path is ever persisted in the database - see the spec note in the original
 * Android BackupManager/FileStorage).
 *
 * [source] is a platform-native reference to the picked file: a content:// URI string on
 * Android, an absolute filesystem path on desktop.
 */
expect class FileStorage(context: PlatformContext) {
    fun copyPickedPhoto(source: String): String?
    fun copyPickedResume(source: String): String?
    fun deleteIfExists(path: String?)
    fun photosDir(): String
    fun resumesDir(): String
}
