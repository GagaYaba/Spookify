package com.ynov.spookify.data.repository

import com.ynov.spookify.data.local.dao.TrackDao
import com.ynov.spookify.data.local.entity.FavoriteEntity
import com.ynov.spookify.data.local.entity.HistoryEntity
import com.ynov.spookify.data.local.entity.TrackRow
import com.ynov.spookify.data.mapper.toEntities
import com.ynov.spookify.data.mapper.toEntity
import com.ynov.spookify.data.mapper.toTrack
import com.ynov.spookify.data.remote.AudiusApi
import com.ynov.spookify.domain.model.AppException
import com.ynov.spookify.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackRepository(
    private val api: AudiusApi,
    private val trackDao: TrackDao,
) {
    fun observeTrending(): Flow<List<Track>> = observeKey(TRENDING_KEY)

    suspend fun refreshTrending(): Result<Unit> = apiCall {
        trackDao.replaceList(TRENDING_KEY, api.getTrendingTracks().data.orEmpty().toEntities())
    }

    fun observeSearch(query: String): Flow<List<Track>> = observeKey(searchKey(query))

    suspend fun refreshSearch(query: String): Result<Unit> = apiCall {
        val tracks = api.searchTracks(query.trim()).data.orEmpty().toEntities()
        // Une seule recherche en cache, pour ne pas accumuler de listes jamais relues.
        trackDao.deleteListsWithPrefix(SEARCH_PREFIX)
        trackDao.replaceList(searchKey(query), tracks)
    }

    fun observeTrack(id: String): Flow<Track?> = trackDao.observeTrack(id).map { it?.toTrack() }

    suspend fun refreshTrack(id: String): Result<Unit> = apiCall {
        val dto = api.getTrack(id).data ?: throw AppException("Morceau introuvable.")
        trackDao.upsertTracks(listOf(dto.toEntity()))
    }

    fun observeFavorites(): Flow<List<Track>> = observeRows(trackDao.observeFavorites())

    fun observeHistory(limit: Int = HISTORY_LIMIT): Flow<List<Track>> = observeRows(trackDao.observeHistory(limit))

    suspend fun setFavorite(trackId: String, favorite: Boolean) {
        if (favorite) {
            trackDao.addFavorite(FavoriteEntity(trackId, System.currentTimeMillis()))
        } else {
            trackDao.removeFavorite(trackId)
        }
    }

    suspend fun recordPlay(trackId: String) {
        trackDao.recordPlay(HistoryEntity(trackId, System.currentTimeMillis()))
    }

    private fun observeKey(key: String): Flow<List<Track>> = observeRows(trackDao.observeList(key))

    private fun observeRows(rows: Flow<List<TrackRow>>): Flow<List<Track>> =
        rows.map { list -> list.map { it.toTrack() } }

    companion object {
        const val TRENDING_KEY = "trending"
        const val SEARCH_PREFIX = "search:"
        private const val HISTORY_LIMIT = 50

        fun searchKey(query: String): String = SEARCH_PREFIX + query.trim().lowercase()
    }
}
