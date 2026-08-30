/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.metro.store.compose.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.metro.store.R

/**
 * Composable to display an indeterminate circular progress indicator
 * @param modifier The modifier to be applied to the composable
 */
@Composable
fun ProgressComposable(modifier: Modifier = Modifier) {
    Text(
        text = "Loading...",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_large))
    )
}

@Preview(showBackground = true)
@Composable
private fun ProgressComposablePreview() {
    ProgressComposable()
}
