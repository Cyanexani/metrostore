/*
 * SPDX-FileCopyrightText: 2025 The Calyx Institute
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.metro.extensions

import com.metro.Constants.PACKAGE_NAME_GMS
import com.aurora.gplayapi.data.models.App

fun App.requiresGMS() = dependencies.dependentPackages.contains(PACKAGE_NAME_GMS)
