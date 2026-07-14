package com.moishe.shadchan.platform

import androidx.compose.runtime.Composable

fun interface PickerLauncher {
    fun launch()
}

/** Pick an existing file (photo, resume, or a .zip backup to restore). Result is a
 *  platform-native reference string (content:// URI on Android, absolute path on desktop),
 *  or null if the user cancelled. */
@Composable
expect fun rememberOpenFileLauncher(onPicked: (String?) -> Unit): PickerLauncher

/** Choose a destination to save a new file (used for creating a backup .zip). Result is a
 *  platform-native reference string, or null if the user cancelled. */
@Composable
expect fun rememberSaveFileLauncher(suggestedName: String, onPicked: (String?) -> Unit): PickerLauncher
