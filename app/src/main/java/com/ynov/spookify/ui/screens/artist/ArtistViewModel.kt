package com.ynov.spookify.ui.screens.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.ynov.spookify.data.repository.ArtistRepository
import com.ynov.spookify.domain.model.Artist
import com.ynov.spookify.domain.model.Track
import com.ynov.spookify.navigation.ArtistDetailRoute
import com.ynov.spookify.ui.spookifyApp
import com.ynov.spookify.ui.state.RefreshStatus
import com.ynov.spookify.ui.state.toRefreshStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArtistViewModel(
    savedStateHandle: SavedStateHandle,
    private val artistRepository: ArtistRepository,
) : ViewModel() {

    private val artistId: String = savedStateHandle.toRoute<ArtistDetailRoute>().id
    private val refreshStatus = MutableStateFlow<RefreshStatus>(RefreshStatus.Running)

    val state: StateFlow<ArtistUiState> = combine(
        artistRepository.observeArtist(artistId),
        artistRepository.observeTracks(artistId),
        refreshStatus,
        ::buildState,
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), ArtistUiState.Chargement)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            refreshStatus.value = RefreshStatus.Running
            val results = listOf(
                async { artistRepository.refreshArtist(artistId) },
                async { artistRepository.refreshTracks(artistId) },
            ).awaitAll()
            refreshStatus.value = results.toRefreshStatus()
        }
    }

    private fun buildState(artist: Artist?, tracks: List<Track>, status: RefreshStatus): ArtistUiState = when {
        artist != null && tracks.isNotEmpty() -> ArtistUiState.Succes(
            artist = artist,
            tracks = tracks,
            staleMessage = (status as? RefreshStatus.Failed)?.message,
        )
        status is RefreshStatus.Running -> ArtistUiState.Chargement
        status is RefreshStatus.Failed -> ArtistUiState.Erreur(status.message)
        else -> ArtistUiState.Vide
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L

        val Factory = viewModelFactory {
            initializer { ArtistViewModel(createSavedStateHandle(), spookifyApp().container.artistRepository) }
        }
    }
}
