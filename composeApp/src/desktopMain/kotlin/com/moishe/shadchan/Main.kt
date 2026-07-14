package com.moishe.shadchan

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.moishe.shadchan.platform.PlatformContext
import com.moishe.shadchan.ui.ShadchanViewModel

fun main() = application {
    val windowState = rememberWindowState(width = 1200.dp, height = 800.dp)

    Window(
        onCloseRequest = ::exitApplication,
        title = "שדכן",
        state = windowState
    ) {
        val viewModel = remember { ShadchanViewModel(PlatformContext()) }
        App(viewModel)
    }
}
