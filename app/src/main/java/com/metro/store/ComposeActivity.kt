/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.metro.store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.content.IntentCompat
import com.metro.store.compose.compositions.UI
import com.metro.store.compose.compositions.LocalUI
import com.metro.store.compose.navigation.NavDisplay
import com.metro.store.compose.navigation.Screen
import com.metro.store.compose.theme.AuroraTheme
import com.metro.store.util.PackageUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // TODO: Change startDestination logic to mirror MainActivity
        val startDestination = IntentCompat.getParcelableExtra(
            intent,
            Screen.PARCEL_KEY,
            Screen::class.java
        ) ?: Screen.Blacklist

        val localUI = when {
            PackageUtil.isTv(this) -> UI.TV
            else -> UI.DEFAULT
        }

        setContent {
            CompositionLocalProvider(LocalUI provides localUI) {
                AuroraTheme {
                    NavDisplay(startDestination = startDestination)
                }
            }
        }
    }
}
