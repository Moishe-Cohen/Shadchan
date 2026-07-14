package com.moishe.shadchan.data

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.moishe.shadchan.platform.PlatformContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val PlatformContext.dataStore by preferencesDataStore(name = "settings")

actual class SettingsRepository actual constructor(private val context: PlatformContext) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val FONT_SIZE = stringPreferencesKey("font_size")
    }

    actual val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM,
            fontSize = prefs[Keys.FONT_SIZE] ?: FontSize.MEDIUM
        )
    }

    actual suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode }
    }

    actual suspend fun setFontSize(size: String) {
        context.dataStore.edit { it[Keys.FONT_SIZE] = size }
    }
}
