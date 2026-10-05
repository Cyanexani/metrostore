/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.aurora.store.R
import com.aurora.store.compose.preview.ThemePreviewProvider

private const val DOT_COUNT = 5
private const val DOT_STAGGER = 0.08f
private const val DOT_TRAVEL = 0.6f

/**
 * Composable to display an indeterminate loading state that fills all available screen, the
 * Windows Phone way: the accent progress dots run along the top and a grey "Loading…" sits
 * top-left where the content will appear.
 * @param modifier The modifier to be applied to the composable
 */
@Composable
fun ContainedLoadingIndicator(modifier: Modifier = Modifier) {
    val description = stringResource(R.string.loading)
    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics { stateDescription = description },
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
    ) {
        MetroProgressDots(modifier = Modifier.fillMaxWidth())
        Text(
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.spacing_medium)),
            text = stringResource(R.string.metro_loading),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Indeterminate Windows Phone progress dots, sized to the width it is given.
 */
@Composable
fun MetroProgressDots(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val transition = rememberInfiniteTransition(label = "metroProgressDots")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = modifier.height(4.dp)) {
        val dot = size.height
        repeat(DOT_COUNT) { index ->
            val local = (progress - index * DOT_STAGGER) / DOT_TRAVEL
            if (local !in 0f..1f) return@repeat

            // Cubic easing around the midpoint: fast at the edges, slow through the centre.
            val centred = 2f * local - 1f
            val eased = 0.5f + 0.5f * centred * centred * centred
            drawRect(
                color = color,
                topLeft = Offset(eased * (size.width + dot) - dot, 0f),
                size = Size(dot, dot)
            )
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun ContainedLoadingIndicatorPreview() {
    ContainedLoadingIndicator()
}
