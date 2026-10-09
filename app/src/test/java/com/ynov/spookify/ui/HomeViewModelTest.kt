package com.ynov.spookify.ui

import com.ynov.spookify.MainDispatcherRule
import com.ynov.spookify.data.repository.PlaylistRepository
import com.ynov.spookify.data.repository.TrackRepository
import com.ynov.spookify.fakes.FakeAudiusApi
import com.ynov.spookify.fakes.FakePlaylistDao
import com.ynov.spookify.fakes.FakeTrackDao
import com.ynov.spookify.fakes.keepCollecting
import com.ynov.spookify.fakes.playlistDto
import com.ynov.spookify.fakes.trackDto
import com.ynov.spookify.ui.screens.home.HomeUiState
import com.ynov.spookify.ui.screens.home.HomeViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api = FakeAudiusApi()

    private fun createViewModel(): HomeViewModel {
        val trackDao = FakeTrackDao()
        return HomeViewModel(TrackRepository(api, trackDao), PlaylistRepository(api, FakePlaylistDao(), trackDao))
    }

    @Test
    fun `shows loading while the first refresh is pending then the content`() = runTest {
        api.gate = CompletableDeferred()
        api.trendingTracks = listOf(trackDto("a"))
        api.trendingPlaylists = listOf(playlistDto("p"))
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        assertEquals(HomeUiState.Chargement, viewModel.state.value)

        api.gate?.complete(Unit)

        val state = assertIs<HomeUiState.Succes>(viewModel.state.value)
        assertEquals(listOf("a"), state.tracks.map { it.id })
        assertEquals(listOf("p"), state.playlists.map { it.id })
        assertFalse(state.isRefreshing)
        assertNull(state.staleMessage)
    }

    @Test
    fun `empty responses give the empty state`() = runTest {
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        assertEquals(HomeUiState.Vide, viewModel.state.value)
    }

    @Test
    fun `failure with an empty cache gives the error state`() = runTest {
        api.failure = IOException()
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        assertEquals(HomeUiState.Erreur("Pas de connexion internet."), viewModel.state.value)
    }

    @Test
    fun `retry after an error loads the content`() = runTest {
        api.failure = IOException()
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        api.failure = null
        api.trendingTracks = listOf(trackDto("a"))
        viewModel.refresh()

        assertIs<HomeUiState.Succes>(viewModel.state.value)
    }

    @Test
    fun `failed refresh with a cache keeps the content and flags it as stale`() = runTest {
        api.trendingTracks = listOf(trackDto("a"))
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        api.failure = IOException()
        viewModel.refresh()

        val state = assertIs<HomeUiState.Succes>(viewModel.state.value)
        assertEquals(listOf("a"), state.tracks.map { it.id })
        assertEquals("Pas de connexion internet.", state.staleMessage)
        assertFalse(state.isRefreshing)
    }

    @Test
    fun `refresh with a cache shows the content while refreshing`() = runTest {
        api.trendingTracks = listOf(trackDto("a"))
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        api.gate = CompletableDeferred()
        viewModel.refresh()

        assertTrue(assertIs<HomeUiState.Succes>(viewModel.state.value).isRefreshing)

        api.gate?.complete(Unit)

        assertFalse(assertIs<HomeUiState.Succes>(viewModel.state.value).isRefreshing)
    }

    @Test
    fun `popular artists are the distinct artists of the trending tracks`() = runTest {
        api.trendingTracks = listOf(
            trackDto("a", userId = "u1"),
            trackDto("b", userId = "u1"),
            trackDto("c", userId = "u2"),
            trackDto("d", userId = ""),
        )
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        val state = assertIs<HomeUiState.Succes>(viewModel.state.value)

        assertEquals(listOf("u1", "u2"), state.artists.map { it.id })
        assertEquals("https://img.test/u1.jpg", state.artists.first().avatarUrl)
    }

    @Test
    fun `playlists alone are enough to show the content`() = runTest {
        api.trendingPlaylists = listOf(playlistDto("p"))
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        val state = assertIs<HomeUiState.Succes>(viewModel.state.value)

        assertTrue(state.tracks.isEmpty())
        assertEquals(1, state.playlists.size)
    }
}
