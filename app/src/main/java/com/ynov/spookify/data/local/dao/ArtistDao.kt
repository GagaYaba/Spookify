package com.ynov.spookify.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ynov.spookify.data.local.entity.ArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
    @Upsert
    suspend fun upsertArtist(artist: ArtistEntity)

    @Query("SELECT * FROM artists WHERE id = :id")
    fun observeArtist(id: String): Flow<ArtistEntity?>
}
