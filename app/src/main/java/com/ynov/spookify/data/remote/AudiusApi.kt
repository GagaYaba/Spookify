package com.ynov.spookify.data.remote

import com.ynov.spookify.data.remote.dto.ApiResponse
import com.ynov.spookify.data.remote.dto.PlaylistDto
import com.ynov.spookify.data.remote.dto.TrackDto
import com.ynov.spookify.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AudiusApi {
    @GET("tracks/trending")
    suspend fun getTrendingTracks(@Query("limit") limit: Int = PAGE_SIZE): ApiResponse<List<TrackDto>>

    @GET("tracks/search")
    suspend fun searchTracks(
        @Query("query") query: String,
        @Query("limit") limit: Int = PAGE_SIZE,
    ): ApiResponse<List<TrackDto>>

    @GET("tracks/{id}")
    suspend fun getTrack(@Path("id") id: String): ApiResponse<TrackDto>

    @GET("playlists/trending")
    suspend fun getTrendingPlaylists(@Query("limit") limit: Int = PAGE_SIZE): ApiResponse<List<PlaylistDto>>

    @GET("playlists/{id}/tracks")
    suspend fun getPlaylistTracks(@Path("id") id: String): ApiResponse<List<TrackDto>>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): ApiResponse<UserDto>

    @GET("users/{id}/tracks")
    suspend fun getUserTracks(
        @Path("id") id: String,
        @Query("limit") limit: Int = PAGE_SIZE,
    ): ApiResponse<List<TrackDto>>

    companion object {
        const val PAGE_SIZE = 30
    }
}
