/*
 * SPDX-FileCopyrightText: 2026 Metro Store
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.aurora.store.R
import com.aurora.store.compose.preview.ThemePreviewProvider

private const val DISABLED_ALPHA = 0.38f

@Composable
private fun metroForeground(enabled: Boolean): Color {
    val color = MaterialTheme.colorScheme.onSurface
    return if (enabled) color else color.copy(alpha = DISABLED_ALPHA)
}

/**
 * Windows Phone toggle switch: a rectangular outline that fills with the accent colour when on,
 * with a solid bar for a thumb sliding between the ends. Drop-in for Material's Switch; pass a
 * null [onCheckedChange] when the surrounding row handles the toggle.
 */
@Composable
fun MetroSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val foreground = metroForeground(enabled)
    val accent = MaterialTheme.colorScheme.primary.let {
        if (enabled) it else it.copy(alpha = DISABLED_ALPHA)
    }
    val position by animateFloatAsState(targetValue = if (checked) 1f else 0f, label = "metroSwitch")
    val toggle = if (onCheckedChange != null) {
        Modifier
            .minimumInteractiveComponentSize()
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange
            )
    } else {
        Modifier
    }

    Canvas(modifier = modifier.then(toggle).size(width = 52.dp, height = 24.dp)) {
        val stroke = 2.dp.toPx()
        val inset = stroke + 2.dp.toPx()
        val thumbWidth = 10.dp.toPx()
        val thumbX = position * (size.width - thumbWidth)

        drawRect(
            color = foreground,
            topLeft = Offset(stroke / 2, stroke / 2),
            size = Size(size.width - stroke, size.height - stroke),
            style = Stroke(stroke)
        )
        if (thumbX > inset) {
            drawRect(
                color = accent,
                topLeft = Offset(inset, inset),
                size = Size(thumbX - inset, size.height - 2 * inset)
            )
        }
        drawRect(color = foreground, topLeft = Offset(thumbX, 0f), size = Size(thumbWidth, size.height))
    }
}

/**
 * Windows Phone check box: a square outline with a check mark drawn in when checked.
 * Drop-in for Material's Checkbox.
 */
@Composable
fun MetroCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val foreground = metroForeground(enabled)
    val toggle = if (onCheckedChange != null) {
        Modifier
            .minimumInteractiveComponentSize()
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = onCheckedChange
            )
    } else {
        Modifier
    }

    Canvas(modifier = modifier.then(toggle).size(22.dp)) {
        val stroke = 2.dp.toPx()
        drawRect(
            color = foreground,
            topLeft = Offset(stroke / 2, stroke / 2),
            size = Size(size.width - stroke, size.height - stroke),
            style = Stroke(stroke)
        )
        if (checked) {
            val check = Path().apply {
                moveTo(size.width * 0.22f, size.height * 0.52f)
                lineTo(size.width * 0.42f, size.height * 0.72f)
                lineTo(size.width * 0.78f, size.height * 0.30f)
            }
            drawPath(check, color = foreground, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Square))
        }
    }
}

/**
 * Windows Phone radio button: a thin circle with a solid dot when selected.
 * Drop-in for Material's RadioButton.
 */
@Composable
fun MetroRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val foreground = metroForeground(enabled)
    val select = if (onClick != null) {
        Modifier
            .minimumInteractiveComponentSize()
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
    } else {
        Modifier
    }

    Canvas(modifier = modifier.then(select).size(22.dp)) {
        val stroke = 2.dp.toPx()
        drawCircle(color = foreground, radius = size.minDimension / 2 - stroke / 2, style = Stroke(stroke))
        if (selected) drawCircle(color = foreground, radius = size.minDimension / 4)
    }
}

/**
 * Windows Phone style filter: plain text that is bright when selected and grey otherwise,
 * like the pivot headers, instead of an outlined chip. Drop-in for Material's FilterChip.
 */
@Composable
fun MetroFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val color = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = DISABLED_ALPHA)
        selected -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    CompositionLocalProvider(
        LocalContentColor provides color,
        LocalTextStyle provides MaterialTheme.typography.titleMedium
    ) {
        Row(
            modifier = modifier
                .minimumInteractiveComponentSize()
                .clickable(enabled = enabled, role = Role.Checkbox, onClick = onClick)
                .padding(
                    horizontal = dimensionResource(R.dimen.spacing_xsmall),
                    vertical = dimensionResource(R.dimen.spacing_xsmall)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xsmall))
        ) {
            leadingIcon?.invoke()
            label()
            trailingIcon?.invoke()
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun MetroControlsPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MetroSwitch(checked = true, onCheckedChange = {})
        MetroSwitch(checked = false, onCheckedChange = {})
        MetroCheckbox(checked = true, onCheckedChange = {})
        MetroRadioButton(selected = true, onClick = {})
        Row {
            MetroFilterChip(selected = true, onClick = {}, label = { Text(text = "most helpful") })
            MetroFilterChip(selected = false, onClick = {}, label = { Text(text = "newest") })
        }
    }
}
