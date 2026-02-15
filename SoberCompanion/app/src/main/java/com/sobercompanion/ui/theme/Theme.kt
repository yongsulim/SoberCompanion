package com.sobercompanion.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Gray10,
    primaryContainer = Green20,
    onPrimaryContainer = Green80,
    secondary = Orange80,
    onSecondary = Gray10,
    secondaryContainer = Orange40,
    onSecondaryContainer = Orange80,
    tertiary = Blue80,
    onTertiary = Gray10,
    tertiaryContainer = Blue40,
    onTertiaryContainer = Blue80,
    background = Gray10,
    onBackground = Gray90,
    surface = Gray20,
    onSurface = Gray90,
    surfaceVariant = Gray20,
    onSurfaceVariant = Gray90,
    error = Error,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green80,
    onPrimaryContainer = Green20,
    secondary = Orange40,
    onSecondary = Color.White,
    secondaryContainer = Orange80,
    onSecondaryContainer = Gray10,
    tertiary = Blue40,
    onTertiary = Color.White,
    tertiaryContainer = Blue80,
    onTertiaryContainer = Gray10,
    background = Gray99,
    onBackground = Gray10,
    surface = Gray95,
    onSurface = Gray10,
    surfaceVariant = Gray90,
    onSurfaceVariant = Gray20,
    error = Error,
    onError = Color.White
)

@Composable
fun SoberCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
