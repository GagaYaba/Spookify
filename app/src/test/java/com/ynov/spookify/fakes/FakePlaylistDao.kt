package com.ynov.spookify.fakes

import com.ynov.spookify.data.local.dao.PlaylistDao
import com.ynov.spookify.data.local.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakePlaylistDao : PlaylistDao() {
    private val playlists = MutableStateFlow<Map<String, PlaylistEntity>>(emptyMap())

    override suspend fun upsertPlaylists(playlists: List<PlaylistEntity>) {
        this.playlists.update { it + playlists.associateBy(PlaylistEntity::id) }
    }

    override suspend fun clearTrendingRanks() {
        playlists.update { current -> current.mapValues { it.value.copy(trendingRank = null) } }
    }

    override fun observeTrending(): Flow<List<PlaylistEntity>> = playlists.map { current ->
        current.values.filter { it.trendingRank != null }.sortedBy { it.trendingRank }
    }

    override fun observePlaylist(id: String): Flow<PlaylistEntity?> = playlists.map { it[id] }
}
