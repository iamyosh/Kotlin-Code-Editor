package com.example.codeeditor.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BlackPrimary,
    onPrimary = WhiteText,
    secondary = DarkPinkAccent,
    onSecondary = WhiteText,
    tertiary = LightPinkAccent,
    onTertiary = WhiteText,
    background = BlackPrimary,
    onBackground = WhiteText,
    surface = GreyBackground,
    onSurface = WhiteText
)

private val LightColorScheme = lightColorScheme(
    primary = BlackPrimary,
    onPrimary = WhiteText,
    secondary = DarkPinkAccent,
    onSecondary = WhiteText,
    tertiary = LightPinkAccent,
    onTertiary = WhiteText,
    background = BlackPrimary,
    onBackground = WhiteText,
    surface = GreyBackground,
    onSurface = WhiteText
)

@Composable
fun CodeEditorTheme(
    darkTheme: Boolean = true, // Force dark theme
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to enforce our theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // Use DarkColorScheme for light theme as well to maintain consistent dark theme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}