package com.metro.store.util

import android.content.Context

object FlavouredUtil : IFlavouredUtil {

    override fun promptMicroGInstall(context: Context): Boolean {
        return false
    }
}