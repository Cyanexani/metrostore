/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-FileCopyrightText: 2026 Metro Store
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.main

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.gplayapi.helpers.contracts.StreamContract
import com.aurora.store.MainViewModel
import com.aurora.store.R
import com.aurora.store.compose.composable.MetroAppBar
import com.aurora.store.compose.composable.MetroAppBarButton
import com.aurora.store.compose.composable.MetroMenuItem
import com.aurora.store.compose.composable.MetroTile
import com.aurora.store.compose.composable.SectionHeader
import com.aurora.store.compose.composition.LocalNetworkStatus
import com.aurora.store.compose.navigation.Destination
import com.aurora.store.compose.ui.apps.CategoriesContent
import com.aurora.store.compose.ui.apps.ForYouContent
import com.aurora.store.compose.ui.apps.StorePage
import com.aurora.store.compose.ui.commons.NetworkScreen
import com.aurora.store.data.model.NetworkStatus
import com.aurora.store.util.Preferences
import com.aurora.store.viewmodel.category.CategoryViewModel
import com.aurora.store.viewmodel.homestream.StreamViewModel
import com.aurora.store.viewmodel.notifications.NotificationsViewModel

private const val PAGE_TYPE_APPS = 0
private const val PAGE_TYPE_GAMES = 1

/** Laps of the panorama on either side of the start, so it can be swiped round and round. */
private const val PANORAMA_LAPS = 500

/**
 * Sections of the Store panorama, left to right.
 */
private enum class HubSection(@StringRes val captionRes: Int? = null) {
    FEATURED,
    QUICK_LINKS(R.string.metro_quick_links),
    CATEGORIES(R.string.tab_categories)
}

/**
 * The Windows Phone Store hub: a looping panorama under a giant "Store" title that drifts
 * slower than the content, with each section peeking in from the right edge. Featured apps
 * come as tile grids, quick links as accent tiles into the apps/games pivots, then categories.
 * @param initialTab The pre-panorama default tab (0 apps, 1 games, 2 updates); games and
 * updates open their page on top of the hub so back still returns here
 */
@Composable
fun MainScreen(
    initialTab: Int = 0,
    mainViewModel: MainViewModel = hiltViewModel(),
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    streamViewModel: StreamViewModel = hiltViewModel(key = "stream_$PAGE_TYPE_APPS"),
    categoryViewModel: CategoryViewModel = hiltViewModel(key = "category_$PAGE_TYPE_APPS"),
    onNavigateTo: (Destination) -> Unit = {}
) {
    val context = LocalContext.current
    val networkStatus = LocalNetworkStatus.current
    val updates by mainViewModel.updateHelper.updates.collectAsStateWithLifecycle(
        initialValue = null
    )
    val updateCount = updates?.size ?: 0
    val notificationCount by notificationsViewModel.unreadCount.collectAsStateWithLifecycle()

    var initialTabHandled by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (initialTabHandled) return@LaunchedEffect
        initialTabHandled = true
        when (initialTab) {
            PAGE_TYPE_GAMES -> onNavigateTo(Destination.StoreSection(PAGE_TYPE_GAMES))
            2 -> onNavigateTo(Destination.MyApps)
        }
    }

    if (networkStatus == NetworkStatus.UNAVAILABLE) {
        NetworkScreen()
        return
    }

    val isForYouEnabled = Preferences.getBoolean(context, Preferences.PREFERENCE_FOR_YOU)
    val sections = HubSection.entries.filter { it != HubSection.FEATURED || isForYouEnabled }

    Scaffold(
        bottomBar = {
            MetroAppBar(
                buttons = listOf(
                    MetroAppBarButton(
                        iconRes = R.drawable.ic_round_search,
                        label = stringResource(R.string.action_search)
                    ) { onNavigateTo(Destination.Search) }
                ),
                menuItems = hubMenuItems(updateCount, notificationCount, onNavigateTo)
            )
        }
    ) { paddingValues ->
        StorePanorama(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize(),
            sections = sections,
            updateCount = updateCount,
            isForYouEnabled = isForYouEnabled,
            streamViewModel = streamViewModel,
            categoryViewModel = categoryViewModel,
            onNavigateTo = onNavigateTo
        )
    }
}

@Composable
private fun StorePanorama(
    modifier: Modifier,
    sections: List<HubSection>,
    updateCount: Int,
    isForYouEnabled: Boolean,
    streamViewModel: StreamViewModel,
    categoryViewModel: CategoryViewModel,
    onNavigateTo: (Destination) -> Unit
) {
    val startPage = sections.size * PANORAMA_LAPS
    val pagerState = rememberPagerState(initialPage = startPage) {
        sections.size * PANORAMA_LAPS * 2
    }

    Column(modifier = modifier) {
        // The panorama title moves at a fraction of the content's speed, sliding off to the
        // left as the user travels through the sections.
        Text(
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.spacing_medium))
                .graphicsLayer {
                    val travelled = pagerState.currentPage - startPage +
                        pagerState.currentPageOffsetFraction
                    translationX = -travelled.mod(sections.size.toFloat()) * 36.dp.toPx()
                },
            text = stringResource(R.string.metro_store_title),
            style = MaterialTheme.typography.displayLarge,
            maxLines = 1,
            softWrap = false
        )

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            contentPadding = PaddingValues(end = 40.dp),
            verticalAlignment = Alignment.Top,
            key = { it }
        ) { page ->
            val section = sections[page % sections.size]
            Column(modifier = Modifier.fillMaxSize()) {
                section.captionRes?.let { SectionHeader(title = stringResource(it), trailing = {}) }
                when (section) {
                    HubSection.FEATURED -> ForYouContent(
                        pageType = PAGE_TYPE_APPS,
                        viewModel = streamViewModel,
                        tiles = true,
                        onAppClick = { onNavigateTo(Destination.AppDetails(it.packageName)) },
                        onHeaderClick = { onNavigateTo(Destination.StreamBrowse(it)) },
                        onClusterScrolled = { cluster ->
                            streamViewModel.observeCluster(
                                StreamContract.Category.APPLICATION,
                                cluster
                            )
                        },
                        onScrolledToEnd = {
                            streamViewModel.observe(
                                StreamContract.Category.APPLICATION,
                                StreamContract.Type.HOME
                            )
                        }
                    )

                    HubSection.QUICK_LINKS -> QuickLinks(
                        updateCount = updateCount,
                        isForYouEnabled = isForYouEnabled,
                        onNavigateTo = onNavigateTo
                    )

                    HubSection.CATEGORIES -> CategoriesContent(
                        pageType = PAGE_TYPE_APPS,
                        viewModel = categoryViewModel,
                        onCategoryClick = { onNavigateTo(Destination.CategoryBrowse(it)) },
                        header = {
                            // Like the WP Store, games are reached from the top of the list.
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onNavigateTo(Destination.StoreSection(PAGE_TYPE_GAMES))
                                    }
                                    .padding(
                                        horizontal = dimensionResource(R.dimen.spacing_medium),
                                        vertical = dimensionResource(R.dimen.spacing_small)
                                    ),
                                text = stringResource(R.string.title_games).lowercase(),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    )
                }
            }
        }
    }
}

private data class QuickLink(val label: String, val count: Int? = null, val destination: Destination)

/**
 * Accent tiles into the apps and games pivots, two per row, headed by my apps (showing the
 * pending update count like a live tile) and downloads.
 */
@Composable
private fun QuickLinks(
    updateCount: Int,
    isForYouEnabled: Boolean,
    onNavigateTo: (Destination) -> Unit
) {
    val apps = stringResource(R.string.title_apps)
    val games = stringResource(R.string.title_games)
    val chartPages = buildList {
        if (isForYouEnabled) add(StorePage.FOR_YOU)
        add(StorePage.TOP_FREE)
        add(StorePage.TRENDING)
        add(StorePage.TOP_PAID)
        add(StorePage.TOP_GROSSING)
    }

    val links = buildList {
        add(
            QuickLink(
                label = if (updateCount > 0) {
                    stringResource(R.string.title_updates)
                } else {
                    stringResource(R.string.metro_my_apps).replaceFirstChar { it.uppercase() }
                },
                count = updateCount.takeIf { it > 0 },
                destination = Destination.MyApps
            )
        )
        add(QuickLink(stringResource(R.string.title_download_manager), destination = Destination.Downloads))
        chartPages.forEach { page ->
            val title = stringResource(page.titleRes)
            add(QuickLink("$title\n$apps", destination = Destination.StoreSection(PAGE_TYPE_APPS, page.ordinal)))
            add(QuickLink("$title\n$games", destination = Destination.StoreSection(PAGE_TYPE_GAMES, page.ordinal)))
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.spacing_medium),
            vertical = dimensionResource(R.dimen.spacing_small)
        ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
    ) {
        items(links.chunked(2)) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))) {
                row.forEach { link ->
                    MetroTile(
                        modifier = Modifier.weight(1f),
                        label = link.label,
                        count = link.count,
                        onClick = { onNavigateTo(link.destination) }
                    )
                }
            }
        }
    }
}

/**
 * The hub's application bar menu: everything the old "more" sheet and toolbar icons offered.
 */
@Composable
private fun hubMenuItems(
    updateCount: Int,
    notificationCount: Int,
    onNavigateTo: (Destination) -> Unit
): List<MetroMenuItem> {
    fun withCount(label: String, count: Int) = if (count > 0) "$label ($count)" else label
    return listOf(
        MetroMenuItem(withCount(stringResource(R.string.metro_my_apps), updateCount)) {
            onNavigateTo(Destination.MyApps)
        },
        MetroMenuItem(stringResource(R.string.title_download_manager)) {
            onNavigateTo(Destination.Downloads)
        },
        MetroMenuItem(withCount(stringResource(R.string.title_notifications), notificationCount)) {
            onNavigateTo(Destination.Notifications)
        },
        MetroMenuItem(stringResource(R.string.title_installed)) {
            onNavigateTo(Destination.Installed)
        },
        MetroMenuItem(stringResource(R.string.title_favourites_manager)) {
            onNavigateTo(Destination.Favourite)
        },
        MetroMenuItem(stringResource(R.string.title_blacklist_manager)) {
            onNavigateTo(Destination.Blacklist)
        },
        MetroMenuItem(stringResource(R.string.title_spoof_manager)) {
            onNavigateTo(Destination.Spoof)
        },
        MetroMenuItem(stringResource(R.string.title_account_manager)) {
            onNavigateTo(Destination.Accounts)
        },
        MetroMenuItem(stringResource(R.string.title_settings)) {
            onNavigateTo(Destination.Settings)
        },
        MetroMenuItem(stringResource(R.string.title_about)) { onNavigateTo(Destination.About) }
    )
}
