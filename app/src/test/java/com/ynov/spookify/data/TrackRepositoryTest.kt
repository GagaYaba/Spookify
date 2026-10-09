package com.ynov.spookify.data

import com.ynov.spookify.data.repository.TrackRepository
import com.ynov.spookify.domain.model.AppException
import com.ynov.spookify.fakes.FakeAudiusApi
import com.ynov.spookify.fakes.FakeTrackDao
import com.ynov.spookify.fakes.trackDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TrackRepositoryTest {
    private val api = FakeAudiusApi()
    private val repository = TrackRepository(api, FakeTrackDao())

    private suspend fun trendingIds() = repository.observeTrending().first().map { it.id }

    @Test
    fun `refresh stores trending tracks in api order`() = runTest {
        api.trendingTracks = listOf(trackDto("c"), trackDto("a"), trackDto("b"))

        assertTrue(repository.refreshTrending().isSuccess)

        assertEquals(listOf("c", "a", "b"), trendingIds())
    }

    @Test
    fun `refresh replaces the previous trending list`() = runTest {
        api.trendingTracks = listOf(trackDto("a"), trackDto("b"))
        repository.refreshTrending()
        api.trendingTracks = listOf(trackDto("c"))
        repository.refreshTrending()

        assertEquals(listOf("c"), trendingIds())
    }

    @Test
    fun `deleted or id-less tracks are not cached`() = runTest {
        api.trendingTracks = listOf(trackDto("a"), trackDto("", title = "sans id"), trackDto("b", deleted = true))
        repository.refreshTrending()

        assertEquals(listOf("a"), trendingIds())
    }

    @Test
    fun `response without data is an empty list, not an error`() = runTest {
        api.trendingTracks = null

        assertTrue(repository.refreshTrending().isSuccess)
        assertEquals(emptyList(), trendingIds())
    }

    @Test
    fun `network failure keeps the cache and reports a french message`() = runTest {
        api.trendingTracks = listOf(trackDto("a"), trackDto("b"))
        repository.refreshTrending()

        api.failure = IOException("offline")
        val result = repository.refreshTrending()

        val error = result.exceptionOrNull()
        assertIs<AppException>(error)
        assertEquals("Pas de connexion internet.", error.message)
        assertEquals(listOf("a", "b"), trendingIds())
    }

    @Test
    fun `non streamable tracks are kept and flagged`() = runTest {
        api.trendingTracks = listOf(trackDto("a", streamable = false))
        repository.refreshTrending()

        assertFalse(repository.observeTrending().first().single().isStreamable)
    }

    @Test
    fun `only the latest search stays cached`() = runTest {
        api.searchResults = listOf(trackDto("a"))
        repository.refreshSearch("daft")
        api.searchResults = listOf(trackDto("b"))
        repository.refreshSearch("punk")

        assertEquals(emptyList(), repository.observeSearch("daft").first())
        assertEquals(listOf("b"), repository.observeSearch("punk").first().map { it.id })
    }

    @Test
    fun `search cache key ignores case and surrounding spaces`() = runTest {
        api.searchResults = listOf(trackDto("a"))
        repository.refreshSearch("Daft ")

        assertEquals(listOf("a"), repository.observeSearch("  daft").first().map { it.id })
        assertEquals(listOf("Daft"), api.searchQueries)
    }

    @Test
    fun `search does not touch the trending list`() = runTest {
        api.trendingTracks = listOf(trackDto("t"))
        repository.refreshTrending()
        api.searchResults = listOf(trackDto("s"))
        repository.refreshSearch("x")

        assertEquals(listOf("t"), trendingIds())
    }

    @Test
    fun `favorite flag follows setFavorite in every list`() = runTest {
        api.trendingTracks = listOf(trackDto("a"), trackDto("b"))
        repository.refreshTrending()

        repository.setFavorite("a", favorite = true)
        assertEquals(listOf(true, false), repository.observeTrending().first().map { it.isFavorite })
        assertEquals(listOf("a"), repository.observeFavorites().first().map { it.id })

        repository.setFavorite("a", favorite = false)
        assertEquals(emptyList(), repository.observeFavorites().first())
        assertFalse(repository.observeTrending().first().first().isFavorite)
    }

    @Test
    fun `favorites survive a trending refresh that drops the track`() = runTest {
        api.trendingTracks = listOf(trackDto("a"))
        repository.refreshTrending()
        repository.setFavorite("a", favorite = true)

        api.trendingTracks = listOf(trackDto("b"))
        repository.refreshTrending()

        assertEquals(listOf("a"), repository.observeFavorites().first().map { it.id })
    }

    @Test
    fun `recorded plays show up in history`() = runTest {
        api.trendingTracks = listOf(trackDto("a"), trackDto("b"))
        repository.refreshTrending()

        repository.recordPlay("b")

        assertEquals(listOf("b"), repository.observeHistory().first().map { it.id })
    }

    @Test
    fun `refreshTrack caches the track for observeTrack`() = runTest {
        api.track = trackDto("solo")

        assertTrue(repository.refreshTrack("solo").isSuccess)

        assertEquals("Titre solo", repository.observeTrack("solo").first()?.title)
    }

    @Test
    fun `refreshTrack without data fails with a readable message`() = runTest {
        api.track = null

        val error = repository.refreshTrack("missing").exceptionOrNull()

        assertIs<AppException>(error)
        assertEquals("Morceau introuvable.", error.message)
    }

    @Test
    fun `tracks that left every list are purged from the cache`() = runTest {
        api.trendingTracks = listOf(trackDto("a"), trackDto("b"))
        repository.refreshTrending()
        api.trendingTracks = listOf(trackDto("c"))
        repository.refreshTrending()

        assertEquals(null, repository.observeTrack("a").first())
        assertEquals("c", repository.observeTrack("c").first()?.id)
    }

    @Test
    fun `favorites and played tracks are kept even when no list references them`() = runTest {
        api.trendingTracks = listOf(trackDto("fav"), trackDto("played"), trackDto("gone"))
        repository.refreshTrending()
        repository.setFavorite("fav", favorite = true)
        repository.recordPlay("played")

        api.trendingTracks = listOf(trackDto("other"))
        repository.refreshTrending()

        assertEquals("fav", repository.observeTrack("fav").first()?.id)
        assertEquals("played", repository.observeTrack("played").first()?.id)
        assertEquals(null, repository.observeTrack("gone").first())
    }
}
