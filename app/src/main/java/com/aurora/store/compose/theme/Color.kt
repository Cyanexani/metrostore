/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Whether the active [MaterialTheme] is dark.
 *
 * Derived from the resolved color scheme rather than [androidx.compose.foundation.isSystemInDarkTheme]
 * so it stays correct when the user forces a light/dark theme that differs from the system setting.
 */
@Composable
@ReadOnlyComposable
private fun isAppInDarkTheme(): Boolean = MaterialTheme.colorScheme.surface.luminance() < 0.5f

/**
 * Amber used to flag warnings/caveats. Lightened in dark theme for adequate contrast.
 */
val warningColor: Color
    @Composable @ReadOnlyComposable
    get() = if (isAppInDarkTheme()) Color(0xFFFFB74D) else Color(0xFFFF7600)

/**
 * Green used to flag positive/success states. Lightened in dark theme for adequate contrast.
 */
val successColor: Color
    @Composable @ReadOnlyComposable
    get() = if (isAppInDarkTheme()) Color(0xFF5BD27A) else Color(0xFF1B8738)

val colorGreen: Color
    @Composable @ReadOnlyComposable
    get() = if (isAppInDarkTheme()) Color(0xFF81C784) else Color(0xFF388E3C)

val colorRed: Color
    @Composable @ReadOnlyComposable
    get() = if (isAppInDarkTheme()) Color(0xFFE57373) else Color(0xFFD32F2F)

/**
 * Windows Phone Store palette: a single accent over a pure black (dark) or white (light)
 * background, with a flat charcoal command bar and neutral greys for secondary text.
 */
val MetroAccent = Color(0xFF008A00)
private val MetroBlack = Color(0xFF000000)
private val MetroWhite = Color(0xFFFFFFFF)
private val MetroCommandBarDark = Color(0xFF1F1F1F)
private val MetroCommandBarLight = Color(0xFFDDDDDD)
private val MetroError = Color(0xFFE51400)

/**
 * Dark Metro scheme. Every container collapses onto black or the command bar grey, so
 * Material components render as flat Metro surfaces instead of tinted tonal layers.
 */
fun metroDarkColorScheme(accent: Color = MetroAccent): ColorScheme = darkColorScheme(
    primary = accent,
    onPrimary = MetroWhite,
    primaryContainer = accent,
    onPrimaryContainer = MetroWhite,
    inversePrimary = accent,
    secondary = accent,
    onSecondary = MetroWhite,
    secondaryContainer = MetroCommandBarDark,
    onSecondaryContainer = MetroWhite,
    tertiary = accent,
    onTertiary = MetroWhite,
    tertiaryContainer = MetroCommandBarDark,
    onTertiaryContainer = MetroWhite,
    background = MetroBlack,
    onBackground = MetroWhite,
    surface = MetroBlack,
    onSurface = MetroWhite,
    surfaceVariant = MetroCommandBarDark,
    onSurfaceVariant = Color(0xFF979797),
    surfaceTint = Color.Transparent,
    inverseSurface = MetroWhite,
    inverseOnSurface = MetroBlack,
    error = MetroError,
    onError = MetroWhite,
    errorContainer = MetroError,
    onErrorContainer = MetroWhite,
    outline = MetroWhite,
    outlineVariant = Color(0xFF3A3A3A),
    scrim = MetroBlack,
    surfaceBright = MetroCommandBarDark,
    surfaceDim = MetroBlack,
    surfaceContainerLowest = MetroBlack,
    surfaceContainerLow = MetroBlack,
    surfaceContainer = MetroCommandBarDark,
    surfaceContainerHigh = MetroCommandBarDark,
    surfaceContainerHighest = Color(0xFF2B2B2B)
)

/**
 * Light Metro scheme, the Windows Phone "light" background: black text on white.
 */
fun metroLightColorScheme(accent: Color = MetroAccent): ColorScheme = lightColorScheme(
    primary = accent,
    onPrimary = MetroWhite,
    primaryContainer = accent,
    onPrimaryContainer = MetroWhite,
    inversePrimary = accent,
    secondary = accent,
    onSecondary = MetroWhite,
    secondaryContainer = MetroCommandBarLight,
    onSecondaryContainer = MetroBlack,
    tertiary = accent,
    onTertiary = MetroWhite,
    tertiaryContainer = MetroCommandBarLight,
    onTertiaryContainer = MetroBlack,
    background = MetroWhite,
    onBackground = MetroBlack,
    surface = MetroWhite,
    onSurface = MetroBlack,
    surfaceVariant = MetroCommandBarLight,
    onSurfaceVariant = Color(0xFF6B6B6B),
    surfaceTint = Color.Transparent,
    inverseSurface = MetroBlack,
    inverseOnSurface = MetroWhite,
    error = MetroError,
    onError = MetroWhite,
    errorContainer = MetroError,
    onErrorContainer = MetroWhite,
    outline = MetroBlack,
    outlineVariant = Color(0xFFCCCCCC),
    scrim = MetroBlack,
    surfaceBright = MetroWhite,
    surfaceDim = MetroCommandBarLight,
    surfaceContainerLowest = MetroWhite,
    surfaceContainerLow = MetroWhite,
    surfaceContainer = MetroCommandBarLight,
    surfaceContainerHigh = MetroCommandBarLight,
    surfaceContainerHighest = Color(0xFFCFCFCF)
)
