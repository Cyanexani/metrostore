/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.metro.store.compose.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.metro.store.R

/**
 * A top app bar composable to be used with Scaffold in different Screen
 * @param modifier The modifier to be applied to the composable
 * @param title Title of the screen
 * @param navigationIcon Icon for the navigation button
 * @param onNavigateUp Action when user clicks the navigation icon
 * @param actions Actions to display on the top app bar (for e.g. menu)
 */
@Composable
fun TopAppBarComposable(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigationIcon: Painter = painterResource(R.drawable.ic_arrow_back),
    onNavigateUp: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    TopAppBar(
        modifier = modifier,
        title = {
            if (title != null) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        navigationIcon = {
            if (onNavigateUp != null) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        painter = navigationIcon,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun TopAppBarComposablePreview() {
    TopAppBarComposable(
        title = stringResource(R.string.title_about),
        onNavigateUp = {}
    )
}
