/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.aurora.store.R
import com.aurora.store.compose.preview.ThemePreviewProvider

/**
 * How [SectionHeader] titles are set. Windows Phone cases headings by where they appear, so a
 * screen picks the style once through [LocalSectionHeaderStyle] for every header inside it.
 */
enum class SectionHeaderStyle {
    /** Store hub captions: small and all caps ("QUICK LINKS"). */
    CAPTION,

    /** Groups on a detail page: sentence case in large grey ("Information"). */
    GROUP,

    /** List group titles: lowercase in grey ("action needed"). */
    SUBHEADER
}

val LocalSectionHeaderStyle = staticCompositionLocalOf { SectionHeaderStyle.CAPTION }

/**
 * Section header row used throughout the app, cased per [LocalSectionHeaderStyle].
 * Title with optional subtitle, an optional
 * trailing slot, and the whole row becomes clickable when [onClick] is non-null. When
 * [trailing] is null and [onClick] is set, a default right-chevron is shown.
 */
@Composable
fun SectionHeader(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    val headerStyle = LocalSectionHeaderStyle.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(
                start = dimensionResource(R.dimen.spacing_medium),
                end = dimensionResource(R.dimen.spacing_medium),
                top = dimensionResource(R.dimen.spacing_small),
                bottom = dimensionResource(R.dimen.spacing_xsmall)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = when (headerStyle) {
                    SectionHeaderStyle.CAPTION -> title.uppercase()
                    SectionHeaderStyle.GROUP -> title
                    SectionHeaderStyle.SUBHEADER -> title.metroLowercase()
                },
                style = when (headerStyle) {
                    SectionHeaderStyle.CAPTION -> MaterialTheme.typography.titleSmall
                    SectionHeaderStyle.GROUP -> MaterialTheme.typography.titleLarge
                    SectionHeaderStyle.SUBHEADER -> MaterialTheme.typography.titleMedium
                },
                color = if (headerStyle == SectionHeaderStyle.CAPTION) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        when {
            trailing != null -> trailing()
            onClick != null -> Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_default)),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun SectionHeaderPreview() {
    SectionHeader(title = "Top Charts", onClick = {})
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun SectionHeaderWithSubtitlePreview() {
    SectionHeader(title = "Permissions", subtitle = "3 requested", onClick = {})
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun SectionHeaderWithActionPreview() {
    SectionHeader(
        title = "3 updates available",
        trailing = { MetroButton(onClick = {}) { Text("Update all".metroLowercase()) } }
    )
}
