package com.moishe.shadchan.platform

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
actual fun rememberOpenFileLauncher(onPicked: (String?) -> Unit): PickerLauncher {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onPicked(uri?.toString())
    }
    return PickerLauncher { launcher.launch("*/*") }
}

@Composable
actual fun rememberSaveFileLauncher(suggestedName: String, onPicked: (String?) -> Unit): PickerLauncher {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
        onPicked(uri?.toString())
    }
    return PickerLauncher { launcher.launch(suggestedName) }
}
