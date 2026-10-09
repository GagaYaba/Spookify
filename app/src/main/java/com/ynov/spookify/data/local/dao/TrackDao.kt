package com.ynov.spookify.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ynov.spookify.data.local.entity.FavoriteEntity
import com.ynov.spookify.data.local.entity.HistoryEntity
import com.ynov.spookify.data.local.entity.TrackEntity
import com.ynov.spookify.data.local.entity.TrackListEntryEntity
import com.ynov.spookify.data.local.entity.TrackRow
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TrackDao {
    @Upsert
    abstract suspend fun upsertTracks(tracks: List<TrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertEntries(entries: List<TrackListEntryEntity>)

    @Query("DELETE FROM track_list_entries WHERE listKey = :listKey")
    abstract suspend fun deleteList(listKey: String)

    @Query("DELETE FROM track_list_entries WHERE listKey LIKE :prefix || '%'")
    abstract suspend fun deleteListsWithPrefix(prefix: String)

    // Une seule transaction : l'UI ne voit jamais une liste à moitié remplacée.
    @Transaction
    open suspend fun replaceList(listKey: String, tracks: List<TrackEntity>) {
        upsertTracks(tracks)
        deleteList(listKey)
        insertEntries(tracks.mapIndexed { index, track -> TrackListEntryEntity(listKey, track.id, index) })
        deleteOrphanTracks()
    }

    // Garde le cache borné : un morceau ne survit que s'il est dans une liste, les favoris ou l'historique.
    @Query(
        """
        DELETE FROM tracks
        WHERE id NOT IN (SELECT trackId FROM track_list_entries)
          AND id NOT IN (SELECT trackId FROM favorites)
          AND id NOT IN (SELECT trackId FROM history)
        """
    )
    abstract suspend fun deleteOrphanTracks()

    @Query(
        """
        SELECT t.*, (f.trackId IS NOT NULL) AS isFavorite
        FROM track_list_entries e
        JOIN tracks t ON t.id = e.trackId
        LEFT JOIN favorites f ON f.trackId = t.id
        WHERE e.listKey = :listKey
        ORDER BY e.position
        """
    )
    abstract fun observeList(listKey: String): Flow<List<TrackRow>>

    @Query(
        """
        SELECT t.*, (f.trackId IS NOT NULL) AS isFavorite
        FROM tracks t
        LEFT JOIN favorites f ON f.trackId = t.id
        WHERE t.id = :id
        """
    )
    abstract fun observeTrack(id: String): Flow<TrackRow?>

    @Query(
        """
        SELECT t.*, 1 AS isFavorite
        FROM favorites f
        JOIN tracks t ON t.id = f.trackId
        ORDER BY f.addedAt DESC
        """
    )
    abstract fun observeFavorites(): Flow<List<TrackRow>>

    @Query(
        """
        SELECT t.*, (f.trackId IS NOT NULL) AS isFavorite
        FROM history h
        JOIN tracks t ON t.id = h.trackId
        LEFT JOIN favorites f ON f.trackId = t.id
        ORDER BY h.playedAt DESC
        LIMIT :limit
        """
    )
    abstract fun observeHistory(limit: Int): Flow<List<TrackRow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE trackId = :trackId")
    abstract suspend fun removeFavorite(trackId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun recordPlay(entry: HistoryEntity)
}
