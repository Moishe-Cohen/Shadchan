package com.moishe.shadchan.platform

import androidx.compose.runtime.Composable
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
actual fun rememberOpenFileLauncher(onPicked: (String?) -> Unit): PickerLauncher {
    return PickerLauncher {
        val chooser = JFileChooser()
        val result = chooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            onPicked(chooser.selectedFile?.absolutePath)
        } else {
            onPicked(null)
        }
    }
}

@Composable
actual fun rememberSaveFileLauncher(suggestedName: String, onPicked: (String?) -> Unit): PickerLauncher {
    return PickerLauncher {
        val chooser = JFileChooser().apply {
            selectedFile = File(suggestedName)
            fileFilter = FileNameExtensionFilter("ZIP archive", "zip")
        }
        val result = chooser.showSaveDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            var path = chooser.selectedFile.absolutePath
            if (!path.endsWith(".zip")) path += ".zip"
            onPicked(path)
        } else {
            onPicked(null)
        }
    }
}
