package com.ynov.spookify.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ynov.spookify.data.repository.PlaylistRepository
import com.ynov.spookify.data.repository.TrackRepository
import com.ynov.spookify.domain.model.Playlist
import com.ynov.spookify.domain.model.Track
import com.ynov.spookify.domain.model.toArtistSummary
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

class HomeViewModel(
    private val trackRepository: TrackRepository,
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {

    private val refreshStatus = MutableStateFlow<RefreshStatus>(RefreshStatus.Running)

    // Le cache Room est la source affichée ; le réseau ne fait que le mettre à jour.
    val state: StateFlow<HomeUiState> = combine(
        trackRepository.observeTrending(),
        playlistRepository.observeTrending(),
        refreshStatus,
        ::buildState,
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), HomeUiState.Chargement)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            refreshStatus.value = RefreshStatus.Running
            val results = listOf(
                async { trackRepository.refreshTrending() },
                async { playlistRepository.refreshTrending() },
            ).awaitAll()
            refreshStatus.value = results.toRefreshStatus()
        }
    }

    private fun buildState(tracks: List<Track>, playlists: List<Playlist>, status: RefreshStatus): HomeUiState = when {
        tracks.isNotEmpty() || playlists.isNotEmpty() -> HomeUiState.Succes(
            playlists = playlists,
            artists = tracks.filter { it.artistId.isNotBlank() }.distinctBy { it.artistId }.map(Track::toArtistSummary),
            tracks = tracks,
            isRefreshing = status is RefreshStatus.Running,
            staleMessage = (status as? RefreshStatus.Failed)?.message,
        )
        status is RefreshStatus.Running -> HomeUiState.Chargement
        status is RefreshStatus.Failed -> HomeUiState.Erreur(status.message)
        else -> HomeUiState.Vide
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L

        val Factory = viewModelFactory {
            initializer {
                val container = spookifyApp().container
                HomeViewModel(container.trackRepository, container.playlistRepository)
            }
        }
    }
}
