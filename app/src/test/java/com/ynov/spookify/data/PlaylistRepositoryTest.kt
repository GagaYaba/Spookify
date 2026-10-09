package com.ynov.spookify.data

import com.ynov.spookify.data.repository.PlaylistRepository
import com.ynov.spookify.fakes.FakeAudiusApi
import com.ynov.spookify.fakes.FakePlaylistDao
import com.ynov.spookify.fakes.FakeTrackDao
import com.ynov.spookify.fakes.playlistDto
import com.ynov.spookify.fakes.trackDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PlaylistRepositoryTest {
    private val api = FakeAudiusApi()
    private val repository = PlaylistRepository(api, FakePlaylistDao(), FakeTrackDao())

    @Test
    fun `trending playlists keep api order`() = runTest {
        api.trendingPlaylists = listOf(playlistDto("b"), playlistDto("a"))

        assertTrue(repository.refreshTrending().isSuccess)

        assertEquals(listOf("b", "a"), repository.observeTrending().first().map { it.id })
    }

    @Test
    fun `a new refresh drops playlists that left the trending but keeps them readable by id`() = runTest {
        api.trendingPlaylists = listOf(playlistDto("old"))
        repository.refreshTrending()
        api.trendingPlaylists = listOf(playlistDto("new"))
        repository.refreshTrending()

        assertEquals(listOf("new"), repository.observeTrending().first().map { it.id })
        assertNotNull(repository.observePlaylist("old").first())
    }

    @Test
    fun `deleted playlists are ignored and missing names get a default`() = runTest {
        api.trendingPlaylists = listOf(playlistDto("a", name = null), playlistDto("b", deleted = true))
        repository.refreshTrending()

        val playlists = repository.observeTrending().first()

        assertEquals(listOf("a"), playlists.map { it.id })
        assertEquals("Playlist sans titre", playlists.single().name)
    }

    @Test
    fun `tracks are cached per playlist`() = runTest {
        api.playlistTracks = listOf(trackDto("t1"), trackDto("t2"))
        repository.refreshTracks("p1")
        api.playlistTracks = listOf(trackDto("t3"))
        repository.refreshTracks("p2")

        assertEquals(listOf("t1", "t2"), repository.observeTracks("p1").first().map { it.id })
        assertEquals(listOf("t3"), repository.observeTracks("p2").first().map { it.id })
    }

    @Test
    fun `http failure is reported and cached tracks stay`() = runTest {
        api.playlistTracks = listOf(trackDto("t1"))
        repository.refreshTracks("p1")

        api.failure = HttpException(Response.error<Any>(500, "".toResponseBody()))
        val result = repository.refreshTracks("p1")

        assertEquals("Le serveur rencontre un problème (500).", result.exceptionOrNull()?.message)
        assertEquals(listOf("t1"), repository.observeTracks("p1").first().map { it.id })
    }
}
