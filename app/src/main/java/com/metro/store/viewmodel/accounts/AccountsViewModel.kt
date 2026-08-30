/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.metro.store.viewmodel.accounts

import androidx.lifecycle.ViewModel
import com.metro.store.data.providers.AuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(val authProvider: AuthProvider) : ViewModel()
