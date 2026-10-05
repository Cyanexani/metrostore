/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-FileCopyrightText: 2024-2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.theme

import android.content.SharedPreferences
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.aurora.store.util.Preferences

private fun metroStyle(
    size: Int,
    lineHeight: Int,
    weight: FontWeight = FontWeight.Light,
    letterSpacing: TextUnit = 0.em
) = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing
)

/**
 * Segoe-like type ramp: large light headings for panorama/pivot titles, light body text and
 * semibold labels for buttons, section captions and the command bar.
 */
private val MetroTypography = Typography(
    displayLarge = metroStyle(58, 62, letterSpacing = (-0.02).em),
    displayMedium = metroStyle(48, 52, letterSpacing = (-0.02).em),
    displaySmall = metroStyle(38, 44, letterSpacing = (-0.01).em),
    headlineLarge = metroStyle(32, 38),
    headlineMedium = metroStyle(28, 34),
    headlineSmall = metroStyle(24, 30),
    titleLarge = metroStyle(22, 28),
    titleMedium = metroStyle(18, 23, FontWeight.Normal),
    titleSmall = metroStyle(15, 20, FontWeight.SemiBold),
    bodyLarge = metroStyle(19, 24),
    bodyMedium = metroStyle(15, 20, FontWeight.Normal),
    bodySmall = metroStyle(13, 17, FontWeight.Normal),
    labelLarge = metroStyle(15, 20, FontWeight.SemiBold),
    labelMedium = metroStyle(12, 16, FontWeight.SemiBold),
    labelSmall = metroStyle(11, 14, FontWeight.Normal)
)

private val MetroSquare = RoundedCornerShape(CornerSize(0))

/**
 * Metro has no rounded corners: every themed shape (cards, sheets, dialogs, chips, FABs) is square.
 */
private val MetroShapes = Shapes(
    extraSmall = MetroSquare,
    small = MetroSquare,
    medium = MetroSquare,
    large = MetroSquare,
    extraLarge = MetroSquare,
    largeIncreased = MetroSquare,
    extraLargeIncreased = MetroSquare,
    extraExtraLarge = MetroSquare
)

/**
 * App theme for Metro Store: the Windows Phone Store visual language on top of
 * [MaterialExpressiveTheme]. The user's light/dark choice picks the white or black Metro
 * background; "dynamic colors" only swaps the accent for the wallpaper's primary color.
 */
@Composable
fun AuroraTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current

    var themeStyle by remember {
        mutableIntStateOf(Preferences.getInteger(context, Preferences.PREFERENCE_THEME_STYLE))
    }
    var dynamicColor by remember {
        mutableStateOf(
            Preferences.getBoolean(
                context,
                Preferences.PREFERENCE_DYNAMIC_COLORS,
                Preferences.dynamicColorsDefault
            )
        )
    }
    DisposableEffect(Unit) {
        val prefs = Preferences.getPrefs(context)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                Preferences.PREFERENCE_THEME_STYLE ->
                    themeStyle = Preferences.getInteger(context, key)

                Preferences.PREFERENCE_DYNAMIC_COLORS ->
                    dynamicColor =
                        Preferences.getBoolean(context, key, Preferences.dynamicColorsDefault)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    val useDynamicColor = dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val darkTheme = when (themeStyle) {
        1 -> false
        2 -> true
        else -> isSystemInDarkTheme()
    }

    val accent = when {
        !useDynamicColor -> MetroAccent
        darkTheme -> dynamicDarkColorScheme(context).primary
        else -> dynamicLightColorScheme(context).primary
    }
    val colorScheme = if (darkTheme) {
        metroDarkColorScheme(accent)
    } else {
        metroLightColorScheme(accent)
    }

    /**
     * Keep the status/navigation bar icon appearance in sync with the theme's light/dark mode.
     *
     * The bars themselves are kept transparent and edge-to-edge by [androidx.activity.enableEdgeToEdge]
     * (called once in the host activity); we deliberately don't touch `window.statusBarColor` /
     * `navigationBarColor` here since those setters are deprecated no-ops on Android 15+ and would
     * otherwise strip the system contrast scrim. Re-applying the appearance is still required because
     * the user can force a light/dark theme that differs from the system, and that isn't known when
     * edge-to-edge is first configured.
     */
    val view = LocalView.current
    val activity = LocalActivity.current
    if (!view.isInEditMode) {
        SideEffect {
            val currentActivity = activity ?: return@SideEffect
            WindowCompat
                .getInsetsController(currentActivity.window, view)
                .apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
        }
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = MetroTypography,
        shapes = MetroShapes,
        content = content
    )
}
