/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.composable.details

import android.text.format.DateUtils
import android.widget.RatingBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.viewinterop.AndroidView
import com.aurora.gplayapi.data.models.Review
import com.aurora.store.R
import com.aurora.store.compose.preview.ReviewPreviewProvider
import com.aurora.store.compose.preview.ThemePreviewProvider

/**
 * Composable for viewing a review about an app
 * @param modifier The modifier to be applied to the composable
 * @param review [Review] about an app
 */
@Composable
fun ReviewListItem(modifier: Modifier = Modifier, review: Review) {
    // Windows Phone reviews: the reviewer's name in large light type, then the stars and date,
    // then the comment. No avatars.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.spacing_medium),
                vertical = dimensionResource(R.dimen.spacing_small)
            )
    ) {
        Text(
            text = review.userName,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        AndroidView(
            factory = { context ->
                RatingBar(context, null, android.R.attr.ratingBarStyleSmall)
            },
            update = { view ->
                view.rating = review.rating.toFloat()
            }
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
        Text(
            text = review.comment,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun ReviewListItemPreview(@PreviewParameter(ReviewPreviewProvider::class) review: Review) {
    ReviewListItem(review = review)
}
