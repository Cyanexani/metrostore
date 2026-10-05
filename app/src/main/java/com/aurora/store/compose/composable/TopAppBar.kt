/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import com.aurora.store.R
import com.aurora.store.compose.preview.ThemePreviewProvider

/**
 * Windows Phone style page header to be used with Scaffold in different Screen: a small
 * all-caps caption (the app or section name) with the navigation and action buttons, above the
 * page title in large lowercase light type.
 * @param modifier The modifier to be applied to the composable
 * @param title Title of the screen, shown large and lowercase; omitted when null
 * @param header Caption above the title; defaults to the store name
 * @param navigationIcon Icon for the navigation button
 * @param showNavigationIcon Whether to show the navigation (back) icon button
 * @param actions Actions to display on the top app bar (for e.g. menu)
 */
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    header: String? = null,
    navigationIcon: Painter = painterResource(R.drawable.ic_arrow_back),
    showNavigationIcon: Boolean = true,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    val activity = LocalActivity.current as? ComponentActivity
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(windowInsets)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showNavigationIcon) {
                IconButton(onClick = { activity?.onBackPressedDispatcher?.onBackPressed() }) {
                    Icon(
                        painter = navigationIcon,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_medium)))
            }
            Text(
                modifier = Modifier.weight(1f),
                text = (header ?: stringResource(R.string.metro_store_title)).uppercase(),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically, content = actions)
        }
        if (title != null) {
            Text(
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.spacing_medium),
                    end = dimensionResource(R.dimen.spacing_medium),
                    bottom = dimensionResource(R.dimen.spacing_small)
                ),
                text = title.lowercase(),
                style = MaterialTheme.typography.displaySmall,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun TopAppBarPreview() {
    TopAppBar(
        title = stringResource(R.string.title_about)
    )
}
