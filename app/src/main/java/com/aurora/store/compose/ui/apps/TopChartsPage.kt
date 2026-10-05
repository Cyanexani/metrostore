/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.apps

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.gplayapi.data.models.App
import com.aurora.gplayapi.data.models.StreamCluster
import com.aurora.gplayapi.helpers.contracts.TopChartsContract
import com.aurora.store.R
import com.aurora.store.compose.composable.MetroProgressDots
import com.aurora.store.compose.composable.Placeholder
import com.aurora.store.compose.composable.ShimmerAppRow
import com.aurora.store.compose.composable.app.LargeAppListItem
import com.aurora.store.compose.preview.AppPreviewProvider
import com.aurora.store.compose.preview.ThemePreviewProvider
import com.aurora.store.data.model.ViewState
import com.aurora.store.data.model.ViewState.Loading.getDataAs
import com.aurora.store.viewmodel.topchart.TopChartViewModel

private const val LOAD_MORE_THRESHOLD = 2

/**
 * Play Store charts, each shown as its own pivot page in Windows Phone style.
 */
internal enum class StoreChart(val chart: TopChartsContract.Chart, @StringRes val titleRes: Int) {
    TOP_FREE(TopChartsContract.Chart.TOP_SELLING_FREE, R.string.tab_top_free),
    TOP_GROSSING(TopChartsContract.Chart.TOP_GROSSING, R.string.tab_top_grossing),
    TRENDING(TopChartsContract.Chart.MOVERS_SHAKERS, R.string.tab_trending),
    TOP_PAID(TopChartsContract.Chart.TOP_SELLING_PAID, R.string.tab_top_paid)
}

@Composable
internal fun TopChartContent(
    pageType: Int,
    chart: StoreChart,
    viewModel: TopChartViewModel,
    onAppClick: (App) -> Unit
) {
    val chartType =
        if (pageType == 1) TopChartsContract.Type.GAME else TopChartsContract.Type.APPLICATION
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cluster = state.getDataAs<StreamCluster?>()
    val listState = rememberLazyListState()

    LaunchedEffect(chart) {
        viewModel.getStreamCluster(chartType, chart.chart)
    }

    val reachedEnd by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = listState.layoutInfo.totalItemsCount
            last >= total - LOAD_MORE_THRESHOLD
        }
    }
    LaunchedEffect(reachedEnd) {
        if (reachedEnd && cluster?.hasNext() == true) {
            viewModel.nextCluster(chartType, chart.chart)
        }
    }

    TopChartsBody(
        state = state,
        cluster = cluster,
        listState = listState,
        onRetry = { viewModel.getStreamCluster(chartType, chart.chart) },
        onAppClick = onAppClick
    )
}

@Composable
private fun TopChartsBody(
    state: ViewState,
    cluster: StreamCluster?,
    listState: LazyListState = rememberLazyListState(),
    onRetry: () -> Unit = {},
    onAppClick: (App) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            state is ViewState.Error -> Placeholder(
                modifier = Modifier.weight(1f),
                painter = painterResource(R.drawable.ic_apps),
                message = stringResource(R.string.no_apps_available),
                actionLabel = stringResource(R.string.action_retry),
                onAction = onRetry
            )

            cluster != null && cluster.clusterAppList.isEmpty() -> Placeholder(
                modifier = Modifier.weight(1f),
                painter = painterResource(R.drawable.ic_apps),
                message = stringResource(R.string.no_apps_available)
            )

            cluster != null -> {
                val apps = cluster.clusterAppList
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    state = listState,
                    contentPadding = PaddingValues(
                        vertical = dimensionResource(R.dimen.spacing_small)
                    ),
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.spacing_xsmall)
                    )
                ) {
                    items(count = apps.size, key = { apps[it].id }) { index ->
                        LargeAppListItem(
                            app = apps[index],
                            onClick = { onAppClick(apps[index]) }
                        )
                    }
                    if (cluster.hasNext()) {
                        item(key = "progress") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(R.dimen.spacing_large)),
                                contentAlignment = Alignment.Center
                            ) {
                                MetroProgressDots(modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }

            else -> LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.spacing_small)),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.spacing_xsmall)
                )
            ) {
                items(8) { ShimmerAppRow() }
            }
        }
    }
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun TopChartsBodyLoadingPreview() {
    TopChartsBody(
        state = ViewState.Loading,
        cluster = null
    )
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun TopChartsBodyLoadedPreview() {
    val app = AppPreviewProvider().values.first()
    val apps = List(5) { i -> app.copy(id = i + 1, packageName = "com.preview.app$i") }
    val cluster = StreamCluster(id = 1, clusterAppList = apps)
    TopChartsBody(
        state = ViewState.Success(cluster),
        cluster = cluster
    )
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun TopChartsBodyEmptyPreview() {
    val cluster = StreamCluster(id = 1, clusterAppList = emptyList())
    TopChartsBody(
        state = ViewState.Success(cluster),
        cluster = cluster
    )
}

@PreviewWrapper(ThemePreviewProvider::class)
@Preview(showBackground = true)
@Composable
private fun TopChartsBodyErrorPreview() {
    TopChartsBody(
        state = ViewState.Error("Network error"),
        cluster = null
    )
}
