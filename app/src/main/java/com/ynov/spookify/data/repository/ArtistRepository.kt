package com.ynov.spookify.data.repository

import com.ynov.spookify.data.local.dao.ArtistDao
import com.ynov.spookify.data.local.dao.TrackDao
import com.ynov.spookify.data.mapper.toArtist
import com.ynov.spookify.data.mapper.toEntities
import com.ynov.spookify.data.mapper.toEntity
import com.ynov.spookify.data.mapper.toTrack
import com.ynov.spookify.data.remote.AudiusApi
import com.ynov.spookify.domain.model.AppException
import com.ynov.spookify.domain.model.Artist
import com.ynov.spookify.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArtistRepository(
    private val api: AudiusApi,
    private val artistDao: ArtistDao,
    private val trackDao: TrackDao,
) {
    fun observeArtist(id: String): Flow<Artist?> = artistDao.observeArtist(id).map { it?.toArtist() }

    suspend fun refreshArtist(id: String): Result<Unit> = apiCall {
        val user = api.getUser(id).data ?: throw AppException("Artiste introuvable.")
        artistDao.upsertArtist(user.toEntity())
    }

    fun observeTracks(artistId: String): Flow<List<Track>> =
        trackDao.observeList(listKey(artistId)).map { rows -> rows.map { it.toTrack() } }

    suspend fun refreshTracks(artistId: String): Result<Unit> = apiCall {
        trackDao.replaceList(listKey(artistId), api.getUserTracks(artistId).data.orEmpty().toEntities())
    }

    private fun listKey(artistId: String) = "artist:$artistId"
}
