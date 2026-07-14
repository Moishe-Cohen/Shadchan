package com.moishe.shadchan.platform

import com.moishe.shadchan.db.DatabaseDriverFactory
import com.moishe.shadchan.db.DatabaseHolder
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

private const val DB_ENTRY = "database/shadchan.db"
private const val PHOTOS_PREFIX = "photos/"
private const val RESUMES_PREFIX = "resumes/"

actual class BackupManager actual constructor(private val context: PlatformContext) {

    private val fileStorage = FileStorage(context)

    private fun dbFile(): File = File(DatabaseDriverFactory.databaseDirectory(), "shadchan.db")

    actual fun createBackup(destination: String): Result<Unit> {
        return try {
            val dbFile = dbFile()
            File(destination).outputStream().use { out ->
                ZipOutputStream(out).use { zip ->
                    if (dbFile.exists()) addFileToZip(zip, dbFile, DB_ENTRY)
                    File(fileStorage.photosDir()).listFiles()?.forEach { addFileToZip(zip, it, PHOTOS_PREFIX + it.name) }
                    File(fileStorage.resumesDir()).listFiles()?.forEach { addFileToZip(zip, it, RESUMES_PREFIX + it.name) }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual fun restoreBackup(source: String): Result<Unit> {
        return try {
            val srcFile = File(source)
            if (!srcFile.exists()) return Result.failure(IllegalStateException("לא ניתן לפתוח את קובץ הגיבוי"))

            DatabaseHolder.closeAndReset()

            val dbFile = dbFile()
            dbFile.parentFile?.mkdirs()
            File(dbFile.path + "-wal").delete()
            File(dbFile.path + "-shm").delete()

            val photosDir = File(fileStorage.photosDir()).apply { listFiles()?.forEach { it.delete() } }
            val resumesDir = File(fileStorage.resumesDir()).apply { listFiles()?.forEach { it.delete() } }

            srcFile.inputStream().use { input ->
                ZipInputStream(input).use { zip ->
                    var entry: ZipEntry? = zip.nextEntry
                    while (entry != null) {
                        val name = entry.name
                        val outFile: File? = when {
                            name == DB_ENTRY -> dbFile
                            name.startsWith(PHOTOS_PREFIX) && name.length > PHOTOS_PREFIX.length ->
                                File(photosDir, name.removePrefix(PHOTOS_PREFIX))
                            name.startsWith(RESUMES_PREFIX) && name.length > RESUMES_PREFIX.length ->
                                File(resumesDir, name.removePrefix(RESUMES_PREFIX))
                            else -> null
                        }
                        if (outFile != null) {
                            outFile.outputStream().use { output -> zip.copyTo(output) }
                        }
                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun addFileToZip(zip: ZipOutputStream, file: File, entryName: String) {
        zip.putNextEntry(ZipEntry(entryName))
        file.inputStream().use { it.copyTo(zip) }
        zip.closeEntry()
    }
}
