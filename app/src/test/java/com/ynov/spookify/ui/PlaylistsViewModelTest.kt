package com.ynov.spookify.ui

import com.ynov.spookify.MainDispatcherRule
import com.ynov.spookify.data.repository.PlaylistRepository
import com.ynov.spookify.fakes.FakeAudiusApi
import com.ynov.spookify.fakes.FakePlaylistDao
import com.ynov.spookify.fakes.FakeTrackDao
import com.ynov.spookify.fakes.keepCollecting
import com.ynov.spookify.fakes.playlistDto
import com.ynov.spookify.ui.screens.playlists.PlaylistsUiState
import com.ynov.spookify.ui.screens.playlists.PlaylistsViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PlaylistsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api = FakeAudiusApi()

    private fun createViewModel() = PlaylistsViewModel(PlaylistRepository(api, FakePlaylistDao(), FakeTrackDao()))

    @Test
    fun `loading then content`() = runTest {
        api.gate = CompletableDeferred()
        api.trendingPlaylists = listOf(playlistDto("a"), playlistDto("b"))
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)
        assertEquals(PlaylistsUiState.Chargement, viewModel.state.value)

        api.gate?.complete(Unit)

        val state = assertIs<PlaylistsUiState.Succes>(viewModel.state.value)
        assertEquals(listOf("a", "b"), state.playlists.map { it.id })
    }

    @Test
    fun `empty result`() = runTest {
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        assertEquals(PlaylistsUiState.Vide, viewModel.state.value)
    }

    @Test
    fun `error then retry`() = runTest {
        api.failure = IOException()
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)
        assertEquals(PlaylistsUiState.Erreur("Pas de connexion internet."), viewModel.state.value)

        api.failure = null
        api.trendingPlaylists = listOf(playlistDto("a"))
        viewModel.refresh()

        assertIs<PlaylistsUiState.Succes>(viewModel.state.value)
    }

    @Test
    fun `cache stays visible when the refresh fails`() = runTest {
        api.trendingPlaylists = listOf(playlistDto("a"))
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        api.failure = IOException()
        viewModel.refresh()

        val state = assertIs<PlaylistsUiState.Succes>(viewModel.state.value)
        assertEquals("Pas de connexion internet.", state.staleMessage)
    }
}
