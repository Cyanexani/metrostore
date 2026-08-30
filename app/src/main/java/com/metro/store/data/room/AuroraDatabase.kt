package com.metro.store.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.metro.store.data.room.download.Download
import com.metro.store.data.room.download.DownloadConverter
import com.metro.store.data.room.download.DownloadDao
import com.metro.store.data.room.favourite.Favourite
import com.metro.store.data.room.favourite.FavouriteDao
import com.metro.store.data.room.update.Update
import com.metro.store.data.room.update.UpdateDao

@Database(
    entities = [Download::class, Favourite::class, Update::class],
    version = 5,
    exportSchema = true
)
@TypeConverters(DownloadConverter::class)
abstract class AuroraDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun updateDao(): UpdateDao
}
