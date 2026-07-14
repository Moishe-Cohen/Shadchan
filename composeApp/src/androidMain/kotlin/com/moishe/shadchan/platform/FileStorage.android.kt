package com.moishe.shadchan.platform

import android.net.Uri
import java.io.File
import java.util.UUID

actual class FileStorage actual constructor(private val context: PlatformContext) {

    actual fun photosDir(): String = File(context.filesDir, "photos").apply { mkdirs() }.absolutePath
    actual fun resumesDir(): String = File(context.filesDir, "resumes").apply { mkdirs() }.absolutePath

    actual fun copyPickedPhoto(source: String): String? = copyPickedFile(source, File(photosDir()))
    actual fun copyPickedResume(source: String): String? = copyPickedFile(source, File(resumesDir()))

    private fun copyPickedFile(source: String, targetDir: File): String? {
        return try {
            val uri = Uri.parse(source)
            val ext = guessExtension(uri) ?: "dat"
            val fileName = "${UUID.randomUUID()}.$ext"
            val outFile = File(targetDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                outFile.outputStream().use { output -> input.copyTo(output) }
            }
            outFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    private fun guessExtension(uri: Uri): String? {
        val type = context.contentResolver.getType(uri) ?: return uri.lastPathSegment?.substringAfterLast('.', "")
        return when (type) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            "application/pdf" -> "pdf"
            "application/msword" -> "doc"
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx"
            else -> uri.lastPathSegment?.substringAfterLast('.', "")
        }
    }

    actual fun deleteIfExists(path: String?) {
        if (path.isNullOrBlank()) return
        val f = File(path)
        if (f.exists()) f.delete()
    }
}
