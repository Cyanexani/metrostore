/*
 * Aurora Store
 *  Copyright (C) 2021, Rahul Kumar Patel <whyorean@gmail.com>
 *  Copyright (C) 2023, grrfe <grrfe@420blaze.it>
 *
 *  Aurora Store is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Aurora Store is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aurora Store.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.metro.store

import android.app.Application
import android.content.Context
import android.util.Log.DEBUG
import android.util.Log.INFO
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.metro.extensions.isPAndAbove
import com.metro.extensions.setAppTheme
import com.metro.store.data.event.EventFlow
import com.metro.store.data.helper.DownloadHelper
import com.metro.store.data.helper.UpdateHelper
import com.metro.store.data.receiver.PackageManagerReceiver
import com.metro.store.util.CommonUtil
import com.metro.store.util.NotificationUtil
import com.metro.store.util.PackageUtil
import com.metro.store.util.Preferences
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import okhttp3.OkHttpClient
import org.lsposed.hiddenapibypass.HiddenApiBypass
import javax.inject.Inject

@HiltAndroidApp
class AuroraApp : Application(), Configuration.Provider, SingletonImageLoader.Factory {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var downloadHelper: DownloadHelper

    @Inject
    lateinit var updateHelper: UpdateHelper

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) DEBUG else INFO)
            .setWorkerFactory(workerFactory)
            .build()

    companion object {
        var scope = MainScope()
            private set

        val enqueuedInstalls: MutableSet<String> = mutableSetOf()
        val events = EventFlow()
    }

    override fun onCreate() {
        super.onCreate()
        // Set the app theme
        val themeStyle = Preferences.getInteger(this, Preferences.PREFERENCE_THEME_STYLE)
        setAppTheme(themeStyle)

        // Apply dynamic colors to activities
        DynamicColors.applyToActivitiesIfAvailable(this)

        // Required for Shizuku installer
        if (isPAndAbove) HiddenApiBypass.addHiddenApiExemptions("I", "L")

        //Create Notification Channels
        NotificationUtil.createNotificationChannel(this)

        // Initialize Download and Update helpers to observe and trigger downloads
        downloadHelper.init()
        updateHelper.init()

        //Register broadcast receiver for package install/uninstall
        ContextCompat.registerReceiver(
            this,
            object : PackageManagerReceiver() {},
            PackageUtil.getFilter(),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        CommonUtil.cleanupInstallationSessions(applicationContext)
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader(this).newBuilder()
            .crossfade(true)
            .components { add(OkHttpNetworkFetcherFactory(callFactory = okHttpClient)) }
            .build()
    }
}
