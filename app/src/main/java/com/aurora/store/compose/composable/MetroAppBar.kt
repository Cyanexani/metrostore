/*
 * SPDX-FileCopyrightText: 2026 Metro Store
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.aurora.store.R
import com.aurora.store.compose.preview.ThemePreviewProvider

/**
 * A circled icon button on the [MetroAppBar].
 */
data class MetroAppBarButton(
    @DrawableRes val iconRes: Int,
    val label: String,
    val badgeCount: Int = 0,
    val onClick: () -> Unit
)

/**
 * A text entry in the [MetroAppBar] menu, revealed by the "…" button.
 */
data class MetroMenuItem(
    val label: String,
    val enabled: Boolean = true,
    val onClick: () -> Unit
)

/**
 * Windows Phone application bar, pinned to the bottom edge. Collapsed it only shows its circled
 * icon buttons (and any [leading] text buttons) with a "…" at the right edge; tapping "…" slides
 * it open to reveal the button labels and a lowercase [menuItems] list, like the WP Store.
 * @param leading Optional text buttons laid out from the left edge, e.g. "install" / "share"
 */
@Composable
fun MetroAppBar(
    modifier: Modifier = Modifier,
    buttons: List<MetroAppBarButton> = emptyList(),
    menuItems: List<MetroMenuItem> = emptyList(),
    leading: (@Composable RowScope.() -> Unit)? = null
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = expanded) { expanded = false }

    Surface(modifier = modifier, color = MaterialTheme.colorScheme.surfaceContainer) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .animateContentSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            ) {
                if (leading != null) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(
                                start = dimensionResource(R.dimen.spacing_medium),
                                end = 48.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.spacing_medium)
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        content = leading
                    )
                }

                if (buttons.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = dimensionResource(R.dimen.spacing_small)),
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.spacing_xlarge)
                        )
                    ) {
                        buttons.forEach { button ->
                            MetroAppBarIconButton(
                                button = button,
                                showLabel = expanded,
                                onClick = {
                                    expanded = false
                                    button.onClick()
                                }
                            )
                        }
                    }
                }

                if (menuItems.isNotEmpty() || buttons.isNotEmpty()) {
                    val description = stringResource(R.string.metro_more_options)
                    MetroEllipsis(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .semantics { contentDescription = description }
                            .clickable(role = Role.Button) { expanded = !expanded }
                            .padding(
                                horizontal = dimensionResource(R.dimen.spacing_large),
                                vertical = dimensionResource(R.dimen.spacing_medium)
                            )
                    )
                }
            }

            AnimatedVisibility(visible = expanded && menuItems.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = dimensionResource(R.dimen.spacing_medium))
                ) {
                    menuItems.forEach { item ->
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = item.enabled) {
                                    expanded = false
                                    item.onClick()
                                }
                                .padding(
                                    horizontal = dimensionResource(R.dimen.spacing_large),
                                    vertical = dimensionResource(R.dimen.spacing_small)
                                ),
                            text = item.label.lowercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = if (item.enabled) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetroAppBarIconButton(
    button: MetroAppBarButton,
    showLabel: Boolean,
    onClick: () -> Unit
) {
    val color = MaterialTheme.colorScheme.onSurface
    Column(
        modifier = Modifier
            .semantics { contentDescription = button.label }
            .clickable(role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BadgedBox(
            badge = {
                if (button.badgeCount > 0) Badge { Text(text = "${button.badgeCount}") }
            }
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .border(width = 2.dp, color = color, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(button.iconRes),
                    contentDescription = null,
                    tint = color
                )
            }
        }
        AnimatedVisibility(visible = showLabel) {
            Text(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.spacing_xsmall)),
                text = button.label.lowercase(),
                style = MaterialTheme.typography.labelSmall,
                color = color,
                maxLines = 1
            )
        }
    }
}

/**
 * The application bar's "…": three small dots, as drawn on Windows Phone.
 */
@Composable
private fun MetroEllipsis(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.onSurface) {
    Canvas(modifier = modifier.size(width = 20.dp, height = 4.dp)) {
        val radius = size.height / 2
        repeat(3) { index ->
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(radius + index * (size.width - 2 * radius) / 2, radius)
            )
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun MetroAppBarPreview() {
    MetroAppBar(
        buttons = listOf(MetroAppBarButton(R.drawable.ic_round_search, "Search") {}),
        menuItems = listOf(MetroMenuItem("My apps") {}, MetroMenuItem("Settings") {})
    )
}
