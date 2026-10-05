/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.aurora.store.R
import com.aurora.store.compose.preview.ThemePreviewProvider

/**
 * Full-screen empty/error placeholder in the Windows Phone style: a small accent glyph and the
 * message in large light grey type, left aligned at the top, with an optional action button.
 * Replaces the previous EmptyState and Error composables.
 */
@Composable
fun Placeholder(
    modifier: Modifier = Modifier,
    painter: Painter,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(R.dimen.spacing_medium),
                vertical = dimensionResource(R.dimen.spacing_large)
            ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium)),
        horizontalAlignment = Alignment.Start
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_default)),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = message,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (actionLabel != null && onAction != null) {
            MetroButton(onClick = onAction) {
                Text(
                    text = actionLabel.metroLowercase(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun PlaceholderPreview() {
    Placeholder(
        painter = painterResource(R.drawable.ic_updates),
        message = stringResource(R.string.details_no_updates),
        actionLabel = stringResource(R.string.check_updates),
        onAction = {}
    )
}
