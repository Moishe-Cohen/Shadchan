package com.moishe.shadchan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.moishe.shadchan.data.FontSize
import com.moishe.shadchan.data.ThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFF2F6690),
    secondary = Color(0xFF81C3D7),
    tertiary = Color(0xFFD4A017),
    background = Color(0xFFF7F8FA),
    surface = Color(0xFFFFFFFF)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF81C3D7),
    secondary = Color(0xFF2F6690),
    tertiary = Color(0xFFD4A017),
    background = Color(0xFF121417),
    surface = Color(0xFF1C1F23)
)

private fun scaleFor(fontSize: String): Float = when (fontSize) {
    FontSize.SMALL -> 0.88f
    FontSize.LARGE -> 1.18f
    else -> 1.0f
}

private fun scaledTypography(scale: Float): Typography {
    val base = Typography()
    fun TextStyle.scaled() = this.copy(fontSize = fontSize * scale, lineHeight = lineHeight * scale)
    return base.copy(
        displayLarge = base.displayLarge.scaled(),
        displayMedium = base.displayMedium.scaled(),
        displaySmall = base.displaySmall.scaled(),
        headlineLarge = base.headlineLarge.scaled(),
        headlineMedium = base.headlineMedium.scaled(),
        headlineSmall = base.headlineSmall.scaled(),
        titleLarge = base.titleLarge.scaled(),
        titleMedium = base.titleMedium.scaled(),
        titleSmall = base.titleSmall.scaled(),
        bodyLarge = base.bodyLarge.scaled(),
        bodyMedium = base.bodyMedium.scaled(),
        bodySmall = base.bodySmall.scaled(),
        labelLarge = base.labelLarge.scaled(),
        labelMedium = base.labelMedium.scaled(),
        labelSmall = base.labelSmall.scaled()
    )
}

@Composable
fun ShadchanTheme(
    themeMode: String = ThemeMode.SYSTEM,
    fontSize: String = FontSize.MEDIUM,
    content: @Composable () -> Unit
) {
    val useDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        else -> isSystemInDarkTheme()
    }
    val colors = if (useDark) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = scaledTypography(scaleFor(fontSize)),
        content = content
    )
}
