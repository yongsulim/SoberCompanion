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
    primary = SageGreen80,
    onPrimary = WarmNeutral10,
    primaryContainer = SageGreen30,
    onPrimaryContainer = SageGreen80,
    secondary = WarmAmber80,
    onSecondary = WarmNeutral10,
    secondaryContainer = WarmAmber40,
    onSecondaryContainer = WarmAmber80,
    tertiary = DustyRose80,
    onTertiary = WarmNeutral10,
    tertiaryContainer = DustyRose40,
    onTertiaryContainer = DustyRose80,
    background = WarmNeutral10,
    onBackground = WarmNeutral90,
    surface = WarmNeutral20,
    onSurface = WarmNeutral90,
    surfaceVariant = WarmNeutral20,
    onSurfaceVariant = WarmNeutral90,
    error = SemanticError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = SageGreen40,
    onPrimary = Color.White,
    primaryContainer = SageGreen80,
    onPrimaryContainer = SageGreen20,
    secondary = WarmAmber40,
    onSecondary = Color.White,
    secondaryContainer = WarmAmber80,
    onSecondaryContainer = WarmNeutral10,
    tertiary = DustyRose40,
    onTertiary = Color.White,
    tertiaryContainer = DustyRose80,
    onTertiaryContainer = WarmNeutral10,
    background = WarmNeutral99,
    onBackground = WarmNeutral10,
    surface = WarmNeutral95,
    onSurface = WarmNeutral10,
    surfaceVariant = WarmNeutral90,
    onSurfaceVariant = WarmNeutral20,
    error = SemanticError,
    onError = Color.White
)

@Composable
fun SoberCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 커스텀 팔레트를 항상 사용하기 위해 다이나믹 컬러 비활성화
    dynamicColor: Boolean = false,
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
