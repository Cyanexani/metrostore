/*
 * SPDX-FileCopyrightText: 2024-2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val MetroGreen = Color(0xFF00A300)
private val MetroBlack = Color.Black
private val MetroWhite = Color(0xFFFFFDFD)
private val MetroGray = Color(0xFF9A9A9A)
private val MetroCommandBar = Color(0xFF202020)

private val MetroColorScheme = darkColorScheme(
    primary = MetroGreen,
    onPrimary = MetroWhite,
    primaryContainer = MetroGreen,
    onPrimaryContainer = MetroWhite,
    secondary = MetroGreen,
    onSecondary = MetroWhite,
    background = MetroBlack,
    onBackground = MetroWhite,
    surface = MetroBlack,
    onSurface = MetroWhite,
    surfaceVariant = MetroCommandBar,
    onSurfaceVariant = MetroGray,
    surfaceContainer = MetroCommandBar,
    outline = MetroWhite,
    outlineVariant = Color(0xFF555555)
)

private val MetroTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 58.sp,
        lineHeight = 62.sp,
        letterSpacing = (-1).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 48.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 38.sp,
        lineHeight = 42.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 32.sp,
        lineHeight = 38.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 25.sp,
        lineHeight = 30.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 23.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 20.sp,
        lineHeight = 25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 17.sp,
        lineHeight = 22.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 19.sp
    )
)

private val MetroShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp)
)

/**
 * App theme for Aurora Store using the Windows Phone Store visual language.
 */
@Composable
fun AuroraTheme(content: @Composable () -> Unit) {
    MaterialExpressiveTheme(
        colorScheme = MetroColorScheme,
        typography = MetroTypography,
        shapes = MetroShapes,
        content = content
    )
}
