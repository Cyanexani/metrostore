/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.details.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.aurora.store.R
import com.aurora.store.compose.composable.MetroButton
import com.aurora.store.compose.composable.metroLowercase
import com.aurora.store.compose.preview.ThemePreviewProvider

/**
 * Composable to display primary and secondary actions available for the app, supposed to be used
 * as a part of the Column with proper vertical arrangement spacing in the AppDetailsScreen.
 * @param primaryActionDisplayName Name of the primary action
 * @param secondaryActionDisplayName Name of the secondary action
 * @param isPrimaryActionEnabled Whether the primary action is enabled
 * @param isSecondaryActionEnabled Whether the secondary action is enabled
 * @param onPrimaryAction Callback when the primary action is clicked
 * @param onSecondaryAction Callback when the secondary action is clicked
 */
@Composable
fun Actions(
    primaryActionDisplayName: String,
    secondaryActionDisplayName: String,
    isPrimaryActionEnabled: Boolean = true,
    isSecondaryActionEnabled: Boolean = true,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: () -> Unit = {}
) {
    // Windows Phone app pages put these in the application bar: compact text buttons from the
    // left edge rather than full-width blocks.
    Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))) {
        val buttonWidthModifier = Modifier.widthIn(min = 96.dp)

        MetroButton(
            modifier = buttonWidthModifier,
            onClick = onPrimaryAction,
            enabled = isPrimaryActionEnabled
        ) {
            Text(
                text = primaryActionDisplayName.metroLowercase(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        MetroButton(
            modifier = buttonWidthModifier,
            onClick = onSecondaryAction,
            enabled = isSecondaryActionEnabled
        ) {
            Text(
                text = secondaryActionDisplayName.metroLowercase(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun ActionsPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
    ) {
        Actions(
            primaryActionDisplayName = stringResource(R.string.action_install),
            secondaryActionDisplayName = stringResource(R.string.title_manual_download)
        )
    }
}
