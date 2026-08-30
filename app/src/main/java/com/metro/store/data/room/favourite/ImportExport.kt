package com.metro.store.data.room.favourite

import com.metro.store.BuildConfig
import kotlinx.serialization.Serializable

@Serializable
data class ImportExport(
    val favourites: List<Favourite>,
    val auroraStoreVersion: Int = BuildConfig.VERSION_CODE,
)
