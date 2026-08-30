/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.metro.store.compose.ui.details

import android.content.ActivityNotFoundException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AdaptStrategy
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.NavigableSupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import coil3.compose.LocalAsyncImagePreviewHandler
import com.metro.Constants.SHARE_URL
import com.metro.extensions.appInfo
import com.metro.extensions.browse
import com.metro.extensions.requiresGMS
import com.metro.extensions.requiresObbDir
import com.metro.extensions.share
import com.metro.extensions.toast
import com.aurora.gplayapi.data.models.App
import com.aurora.gplayapi.data.models.Review
import com.metro.store.R
import com.metro.store.compose.composables.ErrorComposable
import com.metro.store.compose.composables.HeaderComposable
import com.metro.store.compose.composables.ProgressComposable
import com.metro.store.compose.composables.TopAppBarComposable
import com.metro.store.compose.composables.app.AppListComposable
import com.metro.store.compose.navigation.Screen
import com.metro.store.compose.preview.AppPreviewProvider
import com.metro.store.compose.preview.coilPreviewProvider
import com.metro.store.compose.ui.commons.PermissionRationaleScreen
import com.metro.store.compose.ui.details.components.Actions
import com.metro.store.compose.ui.details.components.Changelog
import com.metro.store.compose.ui.details.components.Compatibility
import com.metro.store.compose.ui.details.components.DataSafety
import com.metro.store.compose.ui.details.components.Details
import com.metro.store.compose.ui.details.components.DeveloperDetails
import com.metro.store.compose.ui.details.components.Privacy
import com.metro.store.compose.ui.details.components.RatingAndReviews
import com.metro.store.compose.ui.details.components.Screenshots
import com.metro.store.compose.ui.details.components.Tags
import com.metro.store.compose.ui.details.components.Testing
import com.metro.store.compose.ui.details.menu.AppDetailsMenu
import com.metro.store.compose.ui.details.menu.MenuItem
import com.metro.store.compose.ui.details.navigation.ExtraScreen
import com.metro.store.compose.ui.dev.DevProfileScreen
import com.metro.store.data.installer.AppInstaller
import com.metro.store.data.model.AppState
import com.metro.store.data.model.PermissionType
import com.metro.store.data.model.Report
import com.metro.store.data.model.Scores
import com.metro.store.data.providers.PermissionProvider.Companion.isPermittedToInstall
import com.metro.store.util.PackageUtil
import com.metro.store.util.ShortcutManagerUtil
import com.metro.store.viewmodel.details.AppDetailsViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.aurora.gplayapi.data.models.datasafety.Report as DataSafetyReport

@Composable
fun AppDetailsScreen(
    packageName: String,
    onNavigateUp: () -> Unit,
    onNavigateToAppDetails: (packageName: String) -> Unit,
    viewModel: AppDetailsViewModel = hiltViewModel(key = packageName),
    forceSinglePane: Boolean = false
) {
    val context = LocalContext.current

    val app by viewModel.app.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val featuredReviews by viewModel.featuredReviews.collectAsStateWithLifecycle()
    val favorite by viewModel.favourite.collectAsStateWithLifecycle()
    val exodusReport by viewModel.exodusReport.collectAsStateWithLifecycle()
    val dataSafetyReport by viewModel.dataSafetyReport.collectAsStateWithLifecycle()
    val plexusScores by viewModel.plexusScores.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = packageName) { viewModel.fetchAppDetails(packageName) }

    when (state) {
        is AppState.Loading -> ScreenContentLoading(onNavigateUp = onNavigateUp)
        is AppState.Error -> {
            ScreenContentError(
                onNavigateUp = onNavigateUp,
                message = (state as AppState.Error).message
            )
        }

        else -> {
            ScreenContentApp(
                app = app!!,
                featuredReviews = featuredReviews,
                suggestions = suggestions,
                isFavorite = favorite,
                isAnonymous = viewModel.authProvider.isAnonymous,
                state = state,
                plexusScores = plexusScores,
                dataSafetyReport = dataSafetyReport,
                exodusReport = exodusReport,
                onNavigateUp = onNavigateUp,
                onNavigateToAppDetails = onNavigateToAppDetails,
                onDownload = { viewModel.purchase(app!!) },
                onFavorite = { viewModel.toggleFavourite(app!!) },
                onCancelDownload = { viewModel.cancelDownload(app!!) },
                onUninstall = { AppInstaller.uninstall(context, packageName) },
                onOpen = {
                    try {
                        context.startActivity(
                            PackageUtil.getLaunchIntent(context, packageName)
                        )
                    } catch (_: ActivityNotFoundException) {
                        context.toast(context.getString(R.string.unable_to_open))
                    }
                },
                onTestingSubscriptionChange = { subscribe ->
                    viewModel.updateTestingProgramStatus(packageName, subscribe)
                },
                forceSinglePane = forceSinglePane
            )
        }
    }
}

/**
 * Composable to show progress while fetching app details
 */
@Composable
private fun ScreenContentLoading(onNavigateUp: () -> Unit = {}) {
    Scaffold(
        topBar = { TopAppBarComposable(onNavigateUp = onNavigateUp) }
    ) { paddingValues ->
        ProgressComposable(modifier = Modifier.padding(paddingValues))
    }
}

/**
 * Composable to display errors related to fetching app details
 */
@Composable
private fun ScreenContentError(onNavigateUp: () -> Unit = {}, message: String? = null) {
    Scaffold(
        topBar = { TopAppBarComposable(onNavigateUp = onNavigateUp) }
    ) { paddingValues ->
        ErrorComposable(
            modifier = Modifier.padding(paddingValues),
            icon = painterResource(R.drawable.ic_apps_outage),
            message = message ?: stringResource(R.string.toast_app_unavailable)
        )
    }
}

/**
 * Composable to display app details and suggestions
 */
@Composable
private fun ScreenContentApp(
    app: App,
    featuredReviews: List<Review> = emptyList(),
    suggestions: List<App> = emptyList(),
    isFavorite: Boolean = false,
    isAnonymous: Boolean = true,
    state: AppState = AppState.Unavailable,
    plexusScores: Scores? = null,
    dataSafetyReport: DataSafetyReport? = null,
    exodusReport: Report? = null,
    onNavigateUp: () -> Unit = {},
    onNavigateToAppDetails: (packageName: String) -> Unit = {},
    onDownload: () -> Unit = {},
    onFavorite: () -> Unit = {},
    onCancelDownload: () -> Unit = {},
    onUninstall: () -> Unit = {},
    onOpen: () -> Unit = {},
    onTestingSubscriptionChange: (subscribe: Boolean) -> Unit = {},
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    forceSinglePane: Boolean = false
) {
    val context = LocalContext.current
    var scaffoldDirective = calculatePaneScaffoldDirective(windowAdaptiveInfo)

    if (forceSinglePane) {
        scaffoldDirective = scaffoldDirective.copy(maxHorizontalPartitions = 1)
    }

    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator<NavKey>(
        scaffoldDirective = scaffoldDirective,
        adaptStrategies = SupportingPaneScaffoldDefaults.adaptStrategies(
            supportingPaneAdaptStrategy = AdaptStrategy.Hide
        )
    )
    val coroutineScope = rememberCoroutineScope()
    val shouldShowMenuOnMainPane = scaffoldNavigator
        .scaffoldValue[SupportingPaneScaffoldRole.Supporting] == PaneAdaptedValue.Hidden

    fun onNavigateBack() {
        coroutineScope.launch {
            scaffoldNavigator.navigateBack()
        }
    }

    fun showExtraPane(screen: NavKey) {
        coroutineScope.launch {
            scaffoldNavigator.navigateTo(SupportingPaneScaffoldRole.Extra, screen)
        }
    }

    fun onInstall() {
        if (isPermittedToInstall(context, app)) {
            onDownload()
            onNavigateBack()
        } else {
            val requiredPermissions = setOfNotNull(
                PermissionType.INSTALL_UNKNOWN_APPS,
                if (app.fileList.requiresObbDir()) PermissionType.STORAGE_MANAGER else null,
                if (app.fileList.requiresObbDir()) PermissionType.EXTERNAL_STORAGE else null
            )
            showExtraPane(Screen.PermissionRationale(requiredPermissions = requiredPermissions))
        }
    }

    @Composable
    fun SetupMenu() {
        AppDetailsMenu(isInstalled = app.isInstalled, isFavorite = isFavorite) { menuItem ->
            when (menuItem) {
                MenuItem.FAVORITE -> onFavorite()
                MenuItem.MANUAL_DOWNLOAD -> {
                    showExtraPane(ExtraScreen.ManualDownload)
                }
                MenuItem.SHARE -> context.share(app.displayName, app.packageName)
                MenuItem.APP_INFO -> context.appInfo(app.packageName)
                MenuItem.PLAY_STORE -> context.browse("$SHARE_URL${app.packageName}")
                MenuItem.ADD_TO_HOME -> {
                    ShortcutManagerUtil.requestPinShortcut(context, app.packageName)
                }
            }
        }
    }

    @Composable
    fun SetupActions() {
        when (state) {
            is AppState.Purchasing,
            is AppState.Downloading -> {
                Actions(
                    primaryActionDisplayName = stringResource(R.string.action_open),
                    secondaryActionDisplayName = stringResource(R.string.action_cancel),
                    isPrimaryActionEnabled = false,
                    onSecondaryAction = onCancelDownload,
                    isSecondaryActionEnabled = state !is AppState.Purchasing
                )
            }

            is AppState.Updatable -> {
                Actions(
                    primaryActionDisplayName = stringResource(R.string.action_update),
                    secondaryActionDisplayName = stringResource(R.string.action_share),
                    onPrimaryAction = ::onInstall,
                    onSecondaryAction = { context.share(app.displayName, app.packageName) }
                )
            }

            is AppState.Installed -> {
                Actions(
                    primaryActionDisplayName = stringResource(R.string.action_open),
                    secondaryActionDisplayName = stringResource(R.string.action_share),
                    onPrimaryAction = onOpen,
                    onSecondaryAction = { context.share(app.displayName, app.packageName) }
                )
            }

            else -> {
                val primaryActionName = if (state is AppState.Archived) {
                    stringResource(R.string.action_unarchive)
                } else {
                    if (app.isFree) stringResource(R.string.action_install) else app.price
                }

                Actions(
                    primaryActionDisplayName = primaryActionName,
                    secondaryActionDisplayName = stringResource(R.string.action_share),
                    onPrimaryAction = ::onInstall,
                    onSecondaryAction = { context.share(app.displayName, app.packageName) }
                )
            }
        }
    }

    @Composable
    fun MainPane() {
        Scaffold(
            topBar = {
                TopAppBarComposable(
                    title = app.displayName,
                    onNavigateUp = onNavigateUp,
                    actions = { if (shouldShowMenuOnMainPane) SetupMenu() }
                )
            },
            bottomBar = {
                Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        SetupActions()
                    }
                }
            }
        ) { paddingValues ->
            val pagerState = rememberPagerState(pageCount = { 4 })
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(end = 36.dp),
                pageSpacing = 12.dp
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(dimensionResource(R.dimen.padding_medium)),
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.margin_medium)
                    )
                ) {
                    Text(
                        text = stringResource(
                            when (page) {
                                1 -> R.string.metro_reviews
                                2 -> R.string.metro_details
                                3 -> R.string.metro_related
                                else -> R.string.metro_overview
                            }
                        ),
                        style = MaterialTheme.typography.displayMedium,
                        maxLines = 1
                    )

                    when (page) {
                        0 -> {
                            Details(
                                app = app,
                                state = state,
                                onNavigateToDetailsDevProfile = {
                                    showExtraPane(Screen.DevProfile(it))
                                }
                            )
                            Tags(app = app)
                            Changelog(changelog = app.changes)
                            HeaderComposable(
                                title = stringResource(R.string.details_more_about_app),
                                subtitle = app.shortDescription,
                                onClick = { showExtraPane(ExtraScreen.More) }
                            )
                            Screenshots(
                                screenshots = app.screenshots,
                                onNavigateToScreenshot = {
                                    showExtraPane(ExtraScreen.Screenshot(it))
                                }
                            )
                        }

                        1 -> RatingAndReviews(
                            rating = app.rating,
                            featuredReviews = featuredReviews,
                            onNavigateToDetailsReview = {
                                showExtraPane(ExtraScreen.Review)
                            }
                        )

                        2 -> {
                            if (!isAnonymous && app.testingProgram?.isAvailable == true) {
                                Testing(
                                    isSubscribed = app.testingProgram!!.isSubscribed,
                                    onTestingSubscriptionChange = onTestingSubscriptionChange
                                )
                            }
                            Compatibility(
                                needsGms = app.requiresGMS(),
                                plexusScores = plexusScores
                            )
                            HeaderComposable(
                                title = stringResource(R.string.details_permission),
                                subtitle = if (app.permissions.isNotEmpty()) {
                                    stringResource(
                                        R.string.permissions_requested,
                                        app.permissions.size
                                    )
                                } else {
                                    stringResource(R.string.details_no_permission)
                                },
                                onClick = if (app.permissions.isNotEmpty()) {
                                    { showExtraPane(ExtraScreen.Permission) }
                                } else {
                                    null
                                }
                            )
                            if (dataSafetyReport != null) {
                                DataSafety(
                                    report = dataSafetyReport,
                                    privacyPolicyUrl = app.privacyPolicyUrl
                                )
                            }
                            Privacy(
                                report = exodusReport,
                                onNavigateToDetailsExodus =
                                if (!exodusReport?.trackers.isNullOrEmpty()) {
                                    { showExtraPane(ExtraScreen.Exodus) }
                                } else {
                                    null
                                }
                            )
                            DeveloperDetails(
                                address = app.developerAddress,
                                website = app.developerWebsite,
                                email = app.developerEmail
                            )
                        }

                        else -> suggestions.forEach { suggestion ->
                            AppListComposable(
                                app = suggestion,
                                onClick = {
                                    onNavigateToAppDetails(suggestion.packageName)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SupportingPane() {
        Scaffold(
            topBar = {
                TopAppBarComposable(actions = { if (!shouldShowMenuOnMainPane) SetupMenu() })
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Row(
                    modifier = Modifier.padding(dimensionResource(R.dimen.margin_medium)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_suggestions),
                        contentDescription = null
                    )
                    HeaderComposable(title = stringResource(R.string.pref_ui_similar_apps))
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = dimensionResource(R.dimen.padding_medium))
                ) {
                    items(items = suggestions, key = { item -> item.id }) { app ->
                        AppListComposable(
                            app = app,
                            onClick = { onNavigateToAppDetails(app.packageName) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ExtraPane(screen: NavKey) {
        return when (screen) {
            is ExtraScreen.Review -> ReviewScreen(
                packageName = app.packageName,
                onNavigateUp = ::onNavigateBack
            )

            is ExtraScreen.Exodus -> ExodusScreen(
                packageName = app.packageName,
                onNavigateUp = ::onNavigateBack
            )

            is ExtraScreen.More -> MoreScreen(
                packageName = app.packageName,
                onNavigateUp = ::onNavigateBack,
                onNavigateToAppDetails = onNavigateToAppDetails
            )

            is ExtraScreen.Permission -> PermissionScreen(
                packageName = app.packageName,
                onNavigateUp = ::onNavigateBack
            )

            is ExtraScreen.Screenshot -> ScreenshotScreen(
                packageName = app.packageName,
                index = screen.index,
                onNavigateUp = ::onNavigateBack
            )

            is ExtraScreen.ManualDownload -> ManualDownloadScreen(
                packageName = app.packageName,
                onNavigateUp = ::onNavigateBack
            )

            is Screen.DevProfile -> DevProfileScreen(
                publisherId = app.developerName,
                onNavigateUp = ::onNavigateBack,
                onNavigateToAppDetails = { onNavigateToAppDetails(it) }
            )

            is Screen.PermissionRationale -> PermissionRationaleScreen(
                onNavigateUp = ::onNavigateBack,
                requiredPermissions = screen.requiredPermissions,
                onPermissionCallback = { onInstall() }
            )

            else -> {}
        }
    }

    NavigableSupportingPaneScaffold(
        navigator = scaffoldNavigator,
        mainPane = { AnimatedPane { MainPane() } },
        supportingPane = { AnimatedPane { SupportingPane() } },
        extraPane = {
            scaffoldNavigator.currentDestination?.contentKey?.let { screen ->
                AnimatedPane { ExtraPane(screen) }
            }
        }
    )
}

@PreviewScreenSizes
@Composable
private fun AppDetailsScreenPreview(@PreviewParameter(AppPreviewProvider::class) app: App) {
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides coilPreviewProvider) {
        ScreenContentApp(
            app = app,
            isAnonymous = false,
            suggestions = List(10) { app.copy(id = Random.nextInt()) }
        )
    }
}

@Preview
@Composable
private fun AppDetailsScreenPreviewLoading() {
    ScreenContentLoading()
}

@Preview
@Composable
private fun AppDetailsScreenPreviewError() {
    ScreenContentError()
}
