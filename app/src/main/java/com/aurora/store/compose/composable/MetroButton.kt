/*
 * SPDX-FileCopyrightText: 2026 Metro Store
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.aurora.store.compose.preview.ThemePreviewProvider

/**
 * Windows Phone style button: square, transparent, with a solid foreground-colored border.
 * Used for every filled, tonal and outlined button in the app so actions read consistently.
 * @param colors Optional colors, e.g. [ButtonDefaults.buttonColors] for an accent-filled action
 */
@Composable
fun MetroButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = MetroButtonDefaults.colors(),
    content: @Composable RowScope.() -> Unit
) {
    val borderColor = if (enabled) colors.contentColor else colors.disabledContentColor
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RectangleShape,
        colors = colors,
        border = BorderStroke(2.dp, borderColor),
        content = content
    )
}

object MetroButtonDefaults {

    @Composable
    fun colors(): ButtonColors = ButtonDefaults.outlinedButtonColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun MetroButtonPreview() {
    MetroButton(onClick = {}) { Text(text = "install") }
}
