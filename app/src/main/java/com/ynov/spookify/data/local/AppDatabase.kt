package com.ynov.spookify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ynov.spookify.data.local.dao.ArtistDao
import com.ynov.spookify.data.local.dao.PlaylistDao
import com.ynov.spookify.data.local.dao.TrackDao
import com.ynov.spookify.data.local.entity.ArtistEntity
import com.ynov.spookify.data.local.entity.FavoriteEntity
import com.ynov.spookify.data.local.entity.HistoryEntity
import com.ynov.spookify.data.local.entity.PlaylistEntity
import com.ynov.spookify.data.local.entity.TrackEntity
import com.ynov.spookify.data.local.entity.TrackListEntryEntity

@Database(
    entities = [
        TrackEntity::class,
        TrackListEntryEntity::class,
        FavoriteEntity::class,
        HistoryEntity::class,
        PlaylistEntity::class,
        ArtistEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun artistDao(): ArtistDao
}
