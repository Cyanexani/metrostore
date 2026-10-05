/*
 * SPDX-FileCopyrightText: 2026 Metro Store
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.aurora.store.R
import com.aurora.store.compose.preview.ThemePreviewProvider

/**
 * Square accent tile in the Windows Phone Start screen style, as used by the Store's quick
 * links and "see more": the label sits in the top-left corner and an optional [count] is shown
 * large in the bottom-right corner, like a live tile.
 */
@Composable
fun MetroTile(
    label: String,
    modifier: Modifier = Modifier,
    count: Int? = null,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick)
            .padding(dimensionResource(R.dimen.spacing_small))
    ) {
        Text(
            modifier = Modifier.align(Alignment.TopStart),
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        if (count != null) {
            Text(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = "$count",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun MetroTilePreview() {
    MetroTile(label = "Top free\nApps")
}
