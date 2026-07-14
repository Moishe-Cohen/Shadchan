package com.moishe.shadchan.data

import com.moishe.shadchan.platform.PlatformContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.prefs.Preferences

actual class SettingsRepository actual constructor(private val context: PlatformContext) {
    private val prefs = Preferences.userRoot().node("com/moishe/shadchan")

    private val state = MutableStateFlow(
        AppSettings(
            themeMode = prefs.get("theme_mode", ThemeMode.SYSTEM),
            fontSize = prefs.get("font_size", FontSize.MEDIUM)
        )
    )

    actual val settings: Flow<AppSettings> = state

    actual suspend fun setThemeMode(mode: String) {
        prefs.put("theme_mode", mode)
        state.value = state.value.copy(themeMode = mode)
    }

    actual suspend fun setFontSize(size: String) {
        prefs.put("font_size", size)
        state.value = state.value.copy(fontSize = size)
    }
}
