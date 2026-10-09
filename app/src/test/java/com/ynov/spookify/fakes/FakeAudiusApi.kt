package com.ynov.spookify.fakes

import com.ynov.spookify.data.remote.AudiusApi
import com.ynov.spookify.data.remote.dto.ApiResponse
import com.ynov.spookify.data.remote.dto.PlaylistDto
import com.ynov.spookify.data.remote.dto.TrackDto
import com.ynov.spookify.data.remote.dto.UserDto
import kotlinx.coroutines.CompletableDeferred

/** API en mémoire : chaque réponse est réglable, et [failure] simule une panne sur tous les appels. */
class FakeAudiusApi : AudiusApi {
    var trendingTracks: List<TrackDto>? = emptyList()
    var searchResults: List<TrackDto>? = emptyList()
    var track: TrackDto? = null
    var trendingPlaylists: List<PlaylistDto>? = emptyList()
    var playlistTracks: List<TrackDto>? = emptyList()
    var user: UserDto? = null
    var userTracks: List<TrackDto>? = emptyList()

    var failure: Throwable? = null

    // Tant que [gate] n'est pas complété, les appels restent suspendus : permet d'observer l'état "chargement".
    var gate: CompletableDeferred<Unit>? = null

    val searchQueries = mutableListOf<String>()

    private suspend fun <T> respond(value: T?): ApiResponse<T> {
        gate?.await()
        failure?.let { throw it }
        return ApiResponse(value)
    }

    override suspend fun getTrendingTracks(limit: Int) = respond(trendingTracks)

    override suspend fun searchTracks(query: String, limit: Int): ApiResponse<List<TrackDto>> {
        searchQueries += query
        return respond(searchResults)
    }

    override suspend fun getTrack(id: String) = respond(track)

    override suspend fun getTrendingPlaylists(limit: Int) = respond(trendingPlaylists)

    override suspend fun getPlaylistTracks(id: String) = respond(playlistTracks)

    override suspend fun getUser(id: String) = respond(user)

    override suspend fun getUserTracks(id: String, limit: Int) = respond(userTracks)
}
