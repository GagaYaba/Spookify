package com.ynov.spookify.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ynov.spookify.data.local.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlaylistDao {
    @Upsert
    abstract suspend fun upsertPlaylists(playlists: List<PlaylistEntity>)

    @Query("UPDATE playlists SET trendingRank = NULL")
    abstract suspend fun clearTrendingRanks()

    @Transaction
    open suspend fun replaceTrending(playlists: List<PlaylistEntity>) {
        clearTrendingRanks()
        upsertPlaylists(playlists.mapIndexed { index, playlist -> playlist.copy(trendingRank = index) })
    }

    @Query("SELECT * FROM playlists WHERE trendingRank IS NOT NULL ORDER BY trendingRank")
    abstract fun observeTrending(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    abstract fun observePlaylist(id: String): Flow<PlaylistEntity?>
}
