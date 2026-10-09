package com.ynov.spookify.data.repository

import com.ynov.spookify.data.local.dao.PlaylistDao
import com.ynov.spookify.data.local.dao.TrackDao
import com.ynov.spookify.data.mapper.toEntities
import com.ynov.spookify.data.mapper.toPlaylist
import com.ynov.spookify.data.mapper.toTrack
import com.ynov.spookify.data.remote.AudiusApi
import com.ynov.spookify.domain.model.Playlist
import com.ynov.spookify.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepository(
    private val api: AudiusApi,
    private val playlistDao: PlaylistDao,
    private val trackDao: TrackDao,
) {
    fun observeTrending(): Flow<List<Playlist>> =
        playlistDao.observeTrending().map { list -> list.map { it.toPlaylist() } }

    suspend fun refreshTrending(): Result<Unit> = apiCall {
        playlistDao.replaceTrending(api.getTrendingPlaylists().data.orEmpty().toEntities())
    }

    fun observePlaylist(id: String): Flow<Playlist?> = playlistDao.observePlaylist(id).map { it?.toPlaylist() }

    fun observeTracks(playlistId: String): Flow<List<Track>> =
        trackDao.observeList(listKey(playlistId)).map { rows -> rows.map { it.toTrack() } }

    suspend fun refreshTracks(playlistId: String): Result<Unit> = apiCall {
        trackDao.replaceList(listKey(playlistId), api.getPlaylistTracks(playlistId).data.orEmpty().toEntities())
    }

    private fun listKey(playlistId: String) = "playlist:$playlistId"
}
