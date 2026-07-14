package com.moishe.shadchan.platform

import java.awt.Desktop
import java.io.File

actual class PlatformActions actual constructor(private val context: PlatformContext) {
    actual fun openFile(path: String): Boolean {
        return try {
            val file = File(path)
            if (!file.exists() || !Desktop.isDesktopSupported()) return false
            Desktop.getDesktop().open(file)
            true
        } catch (e: Exception) {
            false
        }
    }
}
