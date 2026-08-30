/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.metro.store.viewmodel.commons

import android.content.Context
import androidx.lifecycle.ViewModel
import com.metro.extensions.isOAndAbove
import com.metro.extensions.isRAndAbove
import com.metro.extensions.isSAndAbove
import com.metro.extensions.isTAndAbove
import com.metro.store.R
import com.metro.store.data.model.Permission
import com.metro.store.data.model.PermissionType
import com.metro.store.data.providers.PermissionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PermissionRationaleViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _permissions = MutableStateFlow(getPermissions(context))
    val permissions = _permissions.asStateFlow()

    private fun getPermissions(context: Context): List<Permission> {
        val list = mutableListOf(
            Permission(
                type = PermissionType.INSTALL_UNKNOWN_APPS,
                title = context.getString(R.string.onboarding_permission_installer),
                subtitle = if (isOAndAbove) {
                    context.getString(R.string.onboarding_permission_installer_desc)
                } else {
                    context.getString(R.string.onboarding_permission_installer_legacy_desc)
                },
                optional = false,
                isGranted = PermissionProvider.Companion.isGranted(
                    context,
                    PermissionType.INSTALL_UNKNOWN_APPS
                )
            ),
            Permission(
                type = PermissionType.DOZE_WHITELIST,
                title = context.getString(R.string.onboarding_permission_doze),
                subtitle = context.getString(R.string.onboarding_permission_doze_desc),
                optional = true,
                isGranted = PermissionProvider.Companion.isGranted(
                    context,
                    PermissionType.DOZE_WHITELIST
                )
            )
        )

        if (isRAndAbove) {
            list.add(
                Permission(
                    type = PermissionType.STORAGE_MANAGER,
                    title = context.getString(R.string.onboarding_permission_esm),
                    subtitle = context.getString(R.string.onboarding_permission_esa_desc),
                    optional = false,
                    isGranted = PermissionProvider.Companion.isGranted(
                        context,
                        PermissionType.STORAGE_MANAGER
                    )
                )
            )
        } else {
            list.add(
                Permission(
                    type = PermissionType.EXTERNAL_STORAGE,
                    title = context.getString(R.string.onboarding_permission_esa),
                    subtitle = context.getString(R.string.onboarding_permission_esa_desc),
                    optional = false,
                    isGranted = PermissionProvider.Companion.isGranted(
                        context,
                        PermissionType.EXTERNAL_STORAGE
                    )
                )
            )
        }

        if (isTAndAbove) {
            list.add(
                Permission(
                    type = PermissionType.POST_NOTIFICATIONS,
                    title = context.getString(R.string.onboarding_permission_notifications),
                    subtitle = context.getString(R.string.onboarding_permission_notifications_desc),
                    optional = true,
                    isGranted = PermissionProvider.Companion.isGranted(
                        context,
                        PermissionType.POST_NOTIFICATIONS
                    )
                )
            )
        }

        if (isSAndAbove) {
            list.add(
                Permission(
                    type = PermissionType.APP_LINKS,
                    title = context.getString(R.string.app_links_title),
                    subtitle = context.getString(R.string.app_links_desc),
                    optional = true,
                    isGranted = PermissionProvider.Companion.isGranted(
                        context,
                        PermissionType.APP_LINKS
                    )
                ),
            )
        }

        return list
    }

    fun refreshPermissionsList() {
        _permissions.value = _permissions.value.map { permission ->
            permission.copy(isGranted = PermissionProvider.Companion.isGranted(
                context,
                permission.type
            )
            )
        }
    }
}
