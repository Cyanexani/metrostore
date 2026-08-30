/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.metro.store.compose.composables.details

import android.text.format.DateUtils
import android.widget.RatingBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.LocalAsyncImagePreviewHandler
import com.aurora.gplayapi.data.models.Review
import com.metro.store.R
import com.metro.store.compose.preview.ReviewPreviewProvider
import com.metro.store.compose.preview.coilPreviewProvider

/**
 * Composable for viewing a review about an app
 * @param modifier The modifier to be applied to the composable
 * @param review [Review] about an app
 */
@Composable
fun ReviewComposable(modifier: Modifier = Modifier, review: Review) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_medium),
                vertical = dimensionResource(R.dimen.padding_small)
            )
    ) {
        Text(
            text = review.userName,
            style = MaterialTheme.typography.headlineLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        AndroidView(
            factory = { context ->
                RatingBar(context, null, android.R.attr.ratingBarStyleSmall)
            },
            update = { view -> view.rating = review.rating.toFloat() }
        )
        Text(
            text = review.comment,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = DateUtils.formatDateTime(
                LocalContext.current,
                review.timeStamp,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewComposablePreview(@PreviewParameter(ReviewPreviewProvider::class) review: Review) {
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides coilPreviewProvider) {
        ReviewComposable(review = review)
    }
}
