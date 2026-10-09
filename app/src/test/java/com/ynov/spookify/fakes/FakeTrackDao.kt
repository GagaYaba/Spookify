package com.ynov.spookify.fakes

import com.ynov.spookify.data.local.dao.TrackDao
import com.ynov.spookify.data.local.entity.FavoriteEntity
import com.ynov.spookify.data.local.entity.HistoryEntity
import com.ynov.spookify.data.local.entity.TrackEntity
import com.ynov.spookify.data.local.entity.TrackListEntryEntity
import com.ynov.spookify.data.local.entity.TrackRow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** DAO en mémoire qui reproduit les jointures des requêtes Room (liste ordonnée, statut favori). */
class FakeTrackDao : TrackDao() {
    private data class Db(
        val tracks: Map<String, TrackEntity> = emptyMap(),
        val entries: List<TrackListEntryEntity> = emptyList(),
        val favorites: Map<String, Long> = emptyMap(),
        val history: Map<String, Long> = emptyMap(),
    ) {
        fun row(id: String): TrackRow? = tracks[id]?.let { TrackRow(it, isFavorite = id in favorites) }
    }

    private val db = MutableStateFlow(Db())

    override suspend fun upsertTracks(tracks: List<TrackEntity>) {
        db.update { it.copy(tracks = it.tracks + tracks.associateBy(TrackEntity::id)) }
    }

    override suspend fun insertEntries(entries: List<TrackListEntryEntity>) {
        db.update { current ->
            val kept = current.entries.filterNot { old ->
                entries.any { it.listKey == old.listKey && it.trackId == old.trackId }
            }
            current.copy(entries = kept + entries)
        }
    }

    override suspend fun deleteList(listKey: String) {
        db.update { current -> current.copy(entries = current.entries.filterNot { it.listKey == listKey }) }
    }

    override suspend fun deleteOrphanTracks() {
        db.update { current ->
            val referenced = current.entries.map { it.trackId }.toSet() + current.favorites.keys + current.history.keys
            current.copy(tracks = current.tracks.filterKeys { it in referenced })
        }
    }

    override suspend fun deleteListsWithPrefix(prefix: String) {
        db.update { current -> current.copy(entries = current.entries.filterNot { it.listKey.startsWith(prefix) }) }
    }

    override fun observeList(listKey: String): Flow<List<TrackRow>> = db.map { current ->
        current.entries
            .filter { it.listKey == listKey }
            .sortedBy { it.position }
            .mapNotNull { current.row(it.trackId) }
    }

    override fun observeTrack(id: String): Flow<TrackRow?> = db.map { it.row(id) }

    override fun observeFavorites(): Flow<List<TrackRow>> = db.map { current ->
        current.favorites.entries
            .sortedByDescending { it.value }
            .mapNotNull { current.row(it.key) }
    }

    override fun observeHistory(limit: Int): Flow<List<TrackRow>> = db.map { current ->
        current.history.entries
            .sortedByDescending { it.value }
            .take(limit)
            .mapNotNull { current.row(it.key) }
    }

    override suspend fun addFavorite(favorite: FavoriteEntity) {
        db.update { it.copy(favorites = it.favorites + (favorite.trackId to favorite.addedAt)) }
    }

    override suspend fun removeFavorite(trackId: String) {
        db.update { it.copy(favorites = it.favorites - trackId) }
    }

    override suspend fun recordPlay(entry: HistoryEntity) {
        db.update { it.copy(history = it.history + (entry.trackId to entry.playedAt)) }
    }
}
