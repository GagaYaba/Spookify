package com.ynov.spookify.ui.screens.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.ynov.spookify.data.repository.PlaylistRepository
import com.ynov.spookify.domain.model.Playlist
import com.ynov.spookify.domain.model.Track
import com.ynov.spookify.navigation.PlaylistDetailRoute
import com.ynov.spookify.ui.spookifyApp
import com.ynov.spookify.ui.state.RefreshStatus
import com.ynov.spookify.ui.state.toRefreshStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {

    private val playlistId: String = savedStateHandle.toRoute<PlaylistDetailRoute>().id
    private val refreshStatus = MutableStateFlow<RefreshStatus>(RefreshStatus.Running)

    val state: StateFlow<PlaylistDetailUiState> = combine(
        playlistRepository.observePlaylist(playlistId),
        playlistRepository.observeTracks(playlistId),
        refreshStatus,
        ::buildState,
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), PlaylistDetailUiState.Chargement)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            refreshStatus.value = RefreshStatus.Running
            refreshStatus.value = playlistRepository.refreshTracks(playlistId).toRefreshStatus()
        }
    }

    private fun buildState(playlist: Playlist?, tracks: List<Track>, status: RefreshStatus): PlaylistDetailUiState = when {
        playlist != null && tracks.isNotEmpty() -> PlaylistDetailUiState.Succes(
            playlist = playlist,
            tracks = tracks,
            staleMessage = (status as? RefreshStatus.Failed)?.message,
        )
        status is RefreshStatus.Running -> PlaylistDetailUiState.Chargement
        status is RefreshStatus.Failed -> PlaylistDetailUiState.Erreur(status.message)
        else -> PlaylistDetailUiState.Vide
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L

        val Factory = viewModelFactory {
            initializer {
                PlaylistDetailViewModel(createSavedStateHandle(), spookifyApp().container.playlistRepository)
            }
        }
    }
}
