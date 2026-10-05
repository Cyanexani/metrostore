/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.apps

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.aurora.gplayapi.helpers.contracts.StreamContract
import com.aurora.store.R
import com.aurora.store.compose.composable.MetroAppBar
import com.aurora.store.compose.composable.MetroAppBarButton
import com.aurora.store.compose.composable.MetroPivotHeader
import com.aurora.store.compose.composable.TopAppBar
import com.aurora.store.compose.navigation.Destination
import com.aurora.store.compose.ui.main.storeMenuItems
import com.aurora.store.util.Preferences
import com.aurora.store.viewmodel.category.CategoryViewModel
import com.aurora.store.viewmodel.homestream.StreamViewModel
import com.aurora.store.viewmodel.topchart.TopChartViewModel
import kotlinx.coroutines.launch

internal fun category(pageType: Int): StreamContract.Category =
    if (pageType == 1) StreamContract.Category.GAME else StreamContract.Category.APPLICATION

/**
 * Pages of the apps / games pivot, in the Windows Phone Store's order: the curated stream, the
 * charts, then the categories. Their ordinals are what [Destination.StoreSection] opens at.
 */
enum class StorePage(@StringRes val titleRes: Int, internal val chart: StoreChart? = null) {
    FOR_YOU(R.string.tab_for_you),
    TOP_FREE(R.string.tab_top_free, StoreChart.TOP_FREE),
    TOP_GROSSING(R.string.tab_top_grossing, StoreChart.TOP_GROSSING),
    TRENDING(R.string.tab_trending, StoreChart.TRENDING),
    TOP_PAID(R.string.tab_top_paid, StoreChart.TOP_PAID),
    CATEGORIES(R.string.tab_categories)
}

/**
 * Windows Phone Store list page for apps or games: an all-caps caption over a single pivot
 * of every page, with search and the Store menu in the application bar.
 * @param pageType 0 for apps, 1 for games
 * @param initialPage [StorePage] ordinal to open at
 */
@Composable
fun AppsGamesScreen(
    pageType: Int,
    initialPage: Int = 0,
    streamViewModel: StreamViewModel = hiltViewModel(key = "stream_$pageType"),
    categoryViewModel: CategoryViewModel = hiltViewModel(key = "category_$pageType"),
    onNavigateTo: (Destination) -> Unit = {}
) {
    val context = LocalContext.current
    val isForYouEnabled = Preferences.getBoolean(context, Preferences.PREFERENCE_FOR_YOU)
    val pages = StorePage.entries.filter { it != StorePage.FOR_YOU || isForYouEnabled }
    val startPage = pages.indexOf(StorePage.entries.getOrNull(initialPage)).coerceAtLeast(0)

    val pagerState = rememberPagerState(initialPage = startPage) { pages.size }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                header = stringResource(if (pageType == 1) R.string.title_games else R.string.title_apps)
            )
        },
        bottomBar = {
            MetroAppBar(
                buttons = listOf(
                    MetroAppBarButton(
                        iconRes = R.drawable.ic_round_search,
                        label = stringResource(R.string.action_search)
                    ) { onNavigateTo(Destination.Search) }
                ),
                menuItems = storeMenuItems(onNavigateTo)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize()
        ) {
            MetroPivotHeader(
                modifier = Modifier.fillMaxWidth(),
                titles = pages.map { stringResource(it.titleRes) },
                selectedIndex = pagerState.currentPage,
                onSelect = { index ->
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                }
            )

            // Like the Windows Phone pivot, pages can be swiped as well as tapped; nested
            // carousels still scroll first and hand the gesture over once they reach their end.
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                verticalAlignment = Alignment.Top
            ) { page ->
                val storePage = pages[page]
                when {
                    storePage == StorePage.FOR_YOU -> ForYouContent(
                        pageType = pageType,
                        viewModel = streamViewModel,
                        tiles = true,
                        onAppClick = { onNavigateTo(Destination.AppDetails(it.packageName)) },
                        onHeaderClick = { onNavigateTo(Destination.StreamBrowse(it)) },
                        onClusterScrolled = { cluster ->
                            streamViewModel.observeCluster(category(pageType), cluster)
                        },
                        onScrolledToEnd = {
                            streamViewModel.observe(category(pageType), StreamContract.Type.HOME)
                        }
                    )

                    storePage == StorePage.CATEGORIES -> CategoriesContent(
                        pageType = pageType,
                        viewModel = categoryViewModel,
                        onCategoryClick = { onNavigateTo(Destination.CategoryBrowse(it)) }
                    )

                    storePage.chart != null -> TopChartContent(
                        pageType = pageType,
                        chart = storePage.chart,
                        viewModel = hiltViewModel<TopChartViewModel>(
                            key = "topChart_${pageType}_${storePage.chart.name}"
                        ),
                        onAppClick = { onNavigateTo(Destination.AppDetails(it.packageName)) }
                    )
                }
            }
        }
    }
}
