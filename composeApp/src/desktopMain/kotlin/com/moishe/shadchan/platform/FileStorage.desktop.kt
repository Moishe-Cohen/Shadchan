package com.moishe.shadchan.platform

import java.io.File
import java.util.UUID

actual class FileStorage actual constructor(private val context: PlatformContext) {

    private fun appDir(): File = File(System.getProperty("user.home"), ".shadchan").apply { mkdirs() }

    actual fun photosDir(): String = File(appDir(), "photos").apply { mkdirs() }.absolutePath
    actual fun resumesDir(): String = File(appDir(), "resumes").apply { mkdirs() }.absolutePath

    actual fun copyPickedPhoto(source: String): String? = copyPickedFile(source, File(photosDir()))
    actual fun copyPickedResume(source: String): String? = copyPickedFile(source, File(resumesDir()))

    private fun copyPickedFile(source: String, targetDir: File): String? {
        return try {
            val srcFile = File(source)
            if (!srcFile.exists()) return null
            val ext = srcFile.extension.ifBlank { "dat" }
            val fileName = "${UUID.randomUUID()}.$ext"
            val outFile = File(targetDir, fileName)
            srcFile.inputStream().use { input -> outFile.outputStream().use { output -> input.copyTo(output) } }
            outFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    actual fun deleteIfExists(path: String?) {
        if (path.isNullOrBlank()) return
        val f = File(path)
        if (f.exists()) f.delete()
    }
}
