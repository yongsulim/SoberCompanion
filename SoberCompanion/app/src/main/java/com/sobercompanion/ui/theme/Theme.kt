package com.sobercompanion.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary            = AppTextPrimary,
    onPrimary          = AppSurface,
    primaryContainer   = AppSurface,
    onPrimaryContainer = AppTextPrimary,
    secondary          = AppTextSecondary,
    onSecondary        = AppSurface,
    secondaryContainer = AppSurface2,
    onSecondaryContainer = AppTextPrimary,
    background         = AppBackground,
    onBackground       = AppTextPrimary,
    surface            = AppSurface,
    onSurface          = AppTextPrimary,
    surfaceVariant     = AppSurface2,
    onSurfaceVariant   = AppTextSecondary,
    outline            = AppBorder,
    inverseSurface     = AppTextPrimary,
    inverseOnSurface   = AppSurface,
    error              = Color(0xFFBA3A2B),
    onError            = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary            = AppDarkTextPrimary,
    onPrimary          = AppDarkSurface,
    primaryContainer   = AppDarkSurface,
    onPrimaryContainer = AppDarkTextPrimary,
    secondary          = AppDarkTextSecondary,
    onSecondary        = AppDarkSurface,
    secondaryContainer = AppDarkSurface2,
    onSecondaryContainer = AppDarkTextPrimary,
    background         = AppDarkBackground,
    onBackground       = AppDarkTextPrimary,
    surface            = AppDarkSurface,
    onSurface          = AppDarkTextPrimary,
    surfaceVariant     = AppDarkSurface2,
    onSurfaceVariant   = AppDarkTextSecondary,
    outline            = AppDarkBorder,
    inverseSurface     = AppDarkTextPrimary,
    inverseOnSurface   = AppDarkSurface,
    error              = Color(0xFFCF6679),
    onError            = Color.Black,
)

@Composable
fun SoberCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

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
