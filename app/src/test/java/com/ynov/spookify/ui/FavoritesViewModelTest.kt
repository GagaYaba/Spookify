package com.ynov.spookify.ui

import com.ynov.spookify.MainDispatcherRule
import com.ynov.spookify.data.repository.TrackRepository
import com.ynov.spookify.fakes.FakeAudiusApi
import com.ynov.spookify.fakes.FakeTrackDao
import com.ynov.spookify.fakes.keepCollecting
import com.ynov.spookify.fakes.trackDto
import com.ynov.spookify.ui.screens.liked.FavoritesUiState
import com.ynov.spookify.ui.screens.liked.FavoritesViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FavoritesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api = FakeAudiusApi()
    private val repository = TrackRepository(api, FakeTrackDao())

    // Créé à la demande : viewModelScope a besoin que la règle ait déjà installé le dispatcher de test.
    private val viewModel by lazy { FavoritesViewModel(repository) }

    @Test
    fun `starts in loading before anyone observes`() {
        assertEquals(FavoritesUiState.Chargement, viewModel.state.value)
    }

    @Test
    fun `no favorite gives the empty state`() = runTest {
        keepCollecting(viewModel.state)

        assertEquals(FavoritesUiState.Vide, viewModel.state.value)
    }

    @Test
    fun `toggling adds then removes a favorite`() = runTest {
        api.trendingTracks = listOf(trackDto("a"), trackDto("b"))
        repository.refreshTrending()
        keepCollecting(viewModel.state)
        val track = repository.observeTrending().first().first()

        viewModel.toggleFavorite(track)

        val state = assertIs<FavoritesUiState.Succes>(viewModel.state.value)
        assertEquals(listOf("a"), state.tracks.map { it.id })

        viewModel.toggleFavorite(state.tracks.single())

        assertEquals(FavoritesUiState.Vide, viewModel.state.value)
    }
}
