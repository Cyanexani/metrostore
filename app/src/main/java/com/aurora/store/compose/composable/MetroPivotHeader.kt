/*
 * SPDX-FileCopyrightText: 2026 Metro Store
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.aurora.store.R
import com.aurora.store.compose.preview.ThemePreviewProvider
import kotlin.math.abs

/**
 * Windows Phone pivot header: lowercase light titles in a single line that runs off the edge of
 * the screen. The selected title is bright, the rest are dimmed, and tapping one selects it.
 *
 * With [rotate] (the default) the selected title always leads and the others wrap around after
 * it, sliding in from the side the user moved towards, exactly like the WP pivot control.
 */
@Composable
fun MetroPivotHeader(
    titles: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    rotate: Boolean = true,
    onSelect: (Int) -> Unit = {}
) {
    if (titles.isEmpty()) return

    val slide = remember { Animatable(0f) }
    val previousIndex = remember { mutableIntStateOf(selectedIndex) }
    LaunchedEffect(selectedIndex) {
        if (!rotate || previousIndex.intValue == selectedIndex) return@LaunchedEffect
        val forward = selectedIndex == (previousIndex.intValue + 1) % titles.size
        previousIndex.intValue = selectedIndex
        slide.snapTo(if (forward) 1f else -1f)
        slide.animateTo(0f, tween(durationMillis = 300, easing = FastOutSlowInEasing))
    }

    val order = if (rotate) {
        titles.indices.map { (selectedIndex + it) % titles.size }
    } else {
        titles.indices.toList()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState(), enabled = !rotate)
            .padding(horizontal = dimensionResource(R.dimen.spacing_medium))
            .graphicsLayer {
                translationX = slide.value * 48.dp.toPx()
                alpha = 1f - abs(slide.value) * 0.5f
            },
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
    ) {
        order.forEach { index ->
            val isSelected = index == selectedIndex
            Text(
                modifier = Modifier
                    .semantics { selected = isSelected }
                    .clickable(role = Role.Tab) { onSelect(index) },
                text = titles[index].metroLowercase(),
                style = style,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                },
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun MetroPivotHeaderPreview() {
    MetroPivotHeader(
        titles = listOf("For you", "Top charts", "Categories"),
        selectedIndex = 1
    )
}
