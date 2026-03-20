package com.birdtracks.farmbird.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Green99,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    secondary = Teal40,
    onSecondary = Teal95,
    secondaryContainer = Teal90,
    onSecondaryContainer = Teal10,
    tertiary = Amber40,
    onTertiary = Amber95,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber10,
    error = Red40,
    onError = Red90,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Green99,
    onBackground = NeutralGrey10,
    surface = Green99,
    onSurface = NeutralGrey10,
    surfaceVariant = NeutralGrey90,
    onSurfaceVariant = NeutralGrey30,
    outline = NeutralGrey50,
    outlineVariant = NeutralGrey80,
    inverseSurface = NeutralGrey20,
    inverseOnSurface = NeutralGrey95,
    inversePrimary = Green80,
    surfaceTint = Green40
)

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green90,
    secondary = Teal80,
    onSecondary = Teal20,
    secondaryContainer = Teal30,
    onSecondaryContainer = Teal90,
    tertiary = Amber80,
    onTertiary = Amber20,
    tertiaryContainer = Amber30,
    onTertiaryContainer = Amber90,
    error = Red80,
    onError = Red10,
    errorContainer = Red40,
    onErrorContainer = Red90,
    // background darker than surface so cards are visually distinct
    background = NeutralGrey10,
    onBackground = NeutralGrey90,
    surface = NeutralGrey20,
    onSurface = NeutralGrey90,
    surfaceVariant = NeutralGrey30,
    onSurfaceVariant = NeutralGrey80,
    outline = NeutralGrey60,
    outlineVariant = NeutralGrey40,
    inverseSurface = NeutralGrey90,
    inverseOnSurface = NeutralGrey20,
    inversePrimary = Green40,
    surfaceTint = Green80
)

@Composable
fun BirdTrackFarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
            @Suppress("DEPRECATION")
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
