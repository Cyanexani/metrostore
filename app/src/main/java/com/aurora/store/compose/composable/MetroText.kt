/*
 * SPDX-FileCopyrightText: 2026 Metro Store
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

/**
 * Names that keep their own capitalisation when a label is lowercased, as the Windows Phone
 * style guide asks for brand and proper names. Longer names come first so "Google Play" wins
 * over "Google".
 */
private val PRESERVED_NAMES = listOf(
    "Google Play", "Play Store", "Metro Store", "Aurora Store", "Aurora Services",
    "F-Droid", "Google", "Shizuku", "Sui", "microG", "Exodus", "Plexus", "Android", "Aurora",
    "Metro", "Wi-Fi", "GitHub", "GitLab", "Telegram", "XDA", "PayPal", "Liberapay", "UPI",
    "APKs", "APK", "OBB", "URL", "VPN", "HTTP", "SOCKS", "GMS", "ADB"
).map { name -> name to Regex("(?<![\\w-])" + Regex.escape(name.lowercase()) + "(?![\\w-])") }

/**
 * Lowercases a label the Windows Phone way: everything goes lowercase except brand and proper
 * names, e.g. "Filter F-Droid apps" becomes "filter F-Droid apps".
 */
fun String.metroLowercase(): String = PRESERVED_NAMES.fold(lowercase()) { text, (name, pattern) ->
    pattern.replace(text, name)
}
