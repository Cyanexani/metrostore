/*
 * SPDX-FileCopyrightText: 2026 Aurora OSS
 * SPDX-FileCopyrightText: 2026 Metro Store
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.aurora.store.compose.ui.updates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aurora.extensions.requiresObbDir
import com.aurora.store.R
import com.aurora.store.compose.composable.InsufficientStorageDialog
import com.aurora.store.compose.composable.LocalSectionHeaderStyle
import com.aurora.store.compose.composable.MetroAppBar
import com.aurora.store.compose.composable.MetroAppBarButton
import com.aurora.store.compose.composable.SectionHeaderStyle
import com.aurora.store.compose.composable.TopAppBar
import com.aurora.store.compose.composable.TrackerUpdateWarningDialog
import com.aurora.store.compose.navigation.Destination
import com.aurora.store.compose.ui.main.storeMenuItems
import com.aurora.store.compose.ui.sheets.AppUpdateSheet
import com.aurora.store.data.model.ExodusTracker
import com.aurora.store.data.model.PermissionType
import com.aurora.store.data.model.StorageRequirement
import com.aurora.store.data.providers.PermissionProvider.Companion.isGranted
import com.aurora.store.data.room.update.Update
import com.aurora.store.util.PackageUtil
import com.aurora.store.util.Preferences
import com.aurora.store.util.Preferences.PREFERENCE_UPDATES_WARN_TRACKERS
import com.aurora.store.util.StorageUtil
import com.aurora.store.viewmodel.all.UpdatesViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * The Windows Phone Store's "my apps" page: pending updates for installed apps, with the
 * update checks (new trackers, storage space, OBB permission) that guard each download.
 */
@Composable
fun MyAppsScreen(
    updatesViewModel: UpdatesViewModel = hiltViewModel(),
    onNavigateTo: (Destination) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val downloads by updatesViewModel.downloadsList.collectAsStateWithLifecycle()

    var appUpdateTarget by remember { mutableStateOf<Update?>(null) }
    var trackerWarning by remember {
        mutableStateOf<Pair<Update, List<ExodusTracker>>?>(null)
    }
    var storageWarning by remember { mutableStateOf<StorageRequirement?>(null) }
    val checkingJobs = remember { mutableStateMapOf<String, Job>() }

    // A blocked update never produces a download, so the effect below can't clear its marker.
    LaunchedEffect(Unit) {
        updatesViewModel.storageWarning.collect {
            storageWarning = it
            checkingJobs.clear()
        }
    }

    // Once the download a check kicked off actually appears, drop the "checking" marker so the
    // item's in-progress state is driven purely by the download (no flash back to "Update").
    LaunchedEffect(downloads) {
        checkingJobs.keys.toList().forEach { pkg ->
            if (downloads.any { it.packageName == pkg && !it.isFinished }) {
                checkingJobs.remove(pkg)
            }
        }
    }

    fun handleNavigation(destination: Destination) {
        when (destination) {
            is Destination.AppUpdate -> appUpdateTarget = destination.update
            else -> onNavigateTo(destination)
        }
    }

    fun performUpdate(update: Update) {
        if (update.fileList.requiresObbDir() &&
            !isGranted(context, PermissionType.STORAGE_MANAGER)
        ) {
            checkingJobs.remove(update.packageName)
            onNavigateTo(
                Destination.PermissionRationale(setOf(PermissionType.STORAGE_MANAGER))
            )
        } else {
            updatesViewModel.download(update)
        }
    }

    appUpdateTarget?.let { app ->
        AppUpdateSheet(
            update = app,
            onDismiss = { appUpdateTarget = null },
            onNavigateTo = { destination ->
                appUpdateTarget = null
                onNavigateTo(destination)
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = stringResource(R.string.title_updates)) },
        bottomBar = {
            MetroAppBar(
                buttons = listOf(
                    MetroAppBarButton(
                        iconRes = R.drawable.ic_refresh,
                        label = stringResource(R.string.check_updates)
                    ) { updatesViewModel.fetchUpdates() },
                    MetroAppBarButton(
                        iconRes = R.drawable.ic_round_search,
                        label = stringResource(R.string.action_search)
                    ) { onNavigateTo(Destination.Search) }
                ),
                menuItems = storeMenuItems(onNavigateTo, showMyApps = false)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize()
        ) {
            CompositionLocalProvider(LocalSectionHeaderStyle provides SectionHeaderStyle.SUBHEADER) {
                UpdatesScreen(
                    viewModel = updatesViewModel,
                    onNavigateTo = ::handleNavigation,
                    onRequestUpdate = { update ->
                        if (!Preferences.getBoolean(
                                context,
                                PREFERENCE_UPDATES_WARN_TRACKERS,
                                false
                            )
                        ) {
                            performUpdate(update)
                        } else {
                            val job = coroutineScope.launch {
                                val installedVc = PackageUtil.getInstalledVersionCode(
                                    context,
                                    update.packageName
                                )
                                val trackers = updatesViewModel.getNewTrackers(
                                    update.packageName,
                                    installedVc
                                )
                                if (trackers.isEmpty()) {
                                    performUpdate(update)
                                } else {
                                    trackerWarning = update to trackers
                                }
                            }
                            checkingJobs[update.packageName] = job
                        }
                    },
                    onRequestUpdateAll = { selectedUpdates ->
                        val needsObb = selectedUpdates.any { it.fileList.requiresObbDir() }
                        if (needsObb && !isGranted(context, PermissionType.STORAGE_MANAGER)) {
                            onNavigateTo(
                                Destination.PermissionRationale(
                                    setOf(PermissionType.STORAGE_MANAGER)
                                )
                            )
                        } else {
                            updatesViewModel.downloadAll(selectedUpdates)
                        }
                    },
                    onCancelUpdate = { packageName ->
                        if (downloads.any { it.packageName == packageName && !it.isFinished }) {
                            checkingJobs.remove(packageName)
                            updatesViewModel.cancelDownload(packageName)
                        } else {
                            checkingJobs.remove(packageName)?.cancel()
                        }
                    },
                    onCancelAll = { updatesViewModel.cancelAll() },
                    checkingPackages = checkingJobs.keys
                )
            }
        }
    }

    trackerWarning?.let { (update, trackers) ->
        TrackerUpdateWarningDialog(
            trackers = trackers,
            onConfirm = {
                val pending = update
                trackerWarning = null
                performUpdate(pending)
            },
            onDismiss = {
                trackerWarning = null
                checkingJobs.remove(update.packageName)
            }
        )
    }

    storageWarning?.let { requirement ->
        InsufficientStorageDialog(
            requirement = requirement,
            onFreeUpSpace = {
                storageWarning = null
                StorageUtil.openFreeUpSpace(context)
            },
            onDismiss = { storageWarning = null }
        )
    }
}
