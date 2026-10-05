/*
 * SPDX-FileCopyrightText: 2026 Metro Store
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable

import android.view.Gravity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.aurora.store.compose.preview.ThemePreviewProvider

/**
 * Windows Phone message box: a full-width charcoal panel anchored to the top of the screen
 * instead of a centred card. Drop-in replacement for [AlertDialog].
 */
@Composable
fun MetroAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            AnchorDialogToTop()
            confirmButton()
        },
        modifier = modifier.fillMaxWidth(),
        dismissButton = dismissButton,
        title = title,
        text = text,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

/**
 * Moves the hosting dialog window to the top edge of the screen.
 */
@Composable
private fun AnchorDialogToTop() {
    val window = (LocalView.current.parent as? DialogWindowProvider)?.window
    SideEffect { window?.setGravity(Gravity.TOP) }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun MetroAlertDialogPreview() {
    MetroAlertDialog(
        onDismissRequest = {},
        confirmButton = { TextButton(onClick = {}) { Text(text = "sign in") } },
        title = { Text(text = "Account required") },
        text = { Text(text = "To use this feature, you'll need to sign in.") }
    )
}
