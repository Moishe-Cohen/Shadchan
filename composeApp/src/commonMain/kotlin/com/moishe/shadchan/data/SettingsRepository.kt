package com.moishe.shadchan.data

import com.moishe.shadchan.platform.PlatformContext
import kotlinx.coroutines.flow.Flow

expect class SettingsRepository(context: PlatformContext) {
    val settings: Flow<AppSettings>
    suspend fun setThemeMode(mode: String)
    suspend fun setFontSize(size: String)
}
