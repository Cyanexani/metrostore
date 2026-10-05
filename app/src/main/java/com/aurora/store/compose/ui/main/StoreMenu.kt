/*
 * SPDX-FileCopyrightText: 2026 Metro Store
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aurora.store.R
import com.aurora.store.compose.composable.MetroMenuItem
import com.aurora.store.compose.navigation.Destination

/**
 * The menu every Store page offers from its application bar "…", as on Windows Phone:
 * home, my apps, downloads and settings.
 */
@Composable
fun storeMenuItems(
    onNavigateTo: (Destination) -> Unit,
    showHome: Boolean = true,
    showMyApps: Boolean = true
): List<MetroMenuItem> = buildList {
    if (showHome) {
        add(MetroMenuItem(stringResource(R.string.metro_home)) { onNavigateTo(Destination.Main(0)) })
    }
    if (showMyApps) {
        add(MetroMenuItem(stringResource(R.string.metro_my_apps)) { onNavigateTo(Destination.MyApps) })
    }
    add(
        MetroMenuItem(stringResource(R.string.title_download_manager)) {
            onNavigateTo(Destination.Downloads)
        }
    )
    add(MetroMenuItem(stringResource(R.string.title_settings)) { onNavigateTo(Destination.Settings) })
}
