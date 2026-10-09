package com.ynov.spookify.fakes

import com.ynov.spookify.data.local.dao.ArtistDao
import com.ynov.spookify.data.local.entity.ArtistEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeArtistDao : ArtistDao {
    private val artists = MutableStateFlow<Map<String, ArtistEntity>>(emptyMap())

    override suspend fun upsertArtist(artist: ArtistEntity) {
        artists.update { it + (artist.id to artist) }
    }

    override fun observeArtist(id: String): Flow<ArtistEntity?> = artists.map { it[id] }
}
