package com.ynov.spookify.data

import com.ynov.spookify.data.remote.dto.UserDto
import com.ynov.spookify.data.repository.ArtistRepository
import com.ynov.spookify.fakes.FakeArtistDao
import com.ynov.spookify.fakes.FakeAudiusApi
import com.ynov.spookify.fakes.FakeTrackDao
import com.ynov.spookify.fakes.trackDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ArtistRepositoryTest {
    private val api = FakeAudiusApi()
    private val repository = ArtistRepository(api, FakeArtistDao(), FakeTrackDao())

    @Test
    fun `artist is cached and mapped`() = runTest {
        api.user = UserDto(id = "u1", name = "Van Snyder", handle = "vs", followerCount = 10, trackCount = 3, bio = "DJ")

        assertTrue(repository.refreshArtist("u1").isSuccess)

        val artist = repository.observeArtist("u1").first()
        assertEquals("Van Snyder", artist?.name)
        assertEquals(10, artist?.followerCount)
        assertEquals("DJ", artist?.bio)
    }

    @Test
    fun `unknown artist before any refresh is null`() = runTest {
        assertNull(repository.observeArtist("nobody").first())
    }

    @Test
    fun `missing artist payload fails with a readable message`() = runTest {
        api.user = null

        assertEquals("Artiste introuvable.", repository.refreshArtist("u1").exceptionOrNull()?.message)
    }

    @Test
    fun `artist tracks are cached per artist`() = runTest {
        api.userTracks = listOf(trackDto("a"), trackDto("b"))
        repository.refreshTracks("u1")
        api.userTracks = listOf(trackDto("c"))
        repository.refreshTracks("u2")

        assertEquals(listOf("a", "b"), repository.observeTracks("u1").first().map { it.id })
        assertEquals(listOf("c"), repository.observeTracks("u2").first().map { it.id })
    }
}
