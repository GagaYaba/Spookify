package com.ynov.spookify.ui.screens.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ynov.spookify.data.repository.PlaylistRepository
import com.ynov.spookify.domain.model.Playlist
import com.ynov.spookify.ui.spookifyApp
import com.ynov.spookify.ui.state.RefreshStatus
import com.ynov.spookify.ui.state.toRefreshStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistRepository: PlaylistRepository) : ViewModel() {

    private val refreshStatus = MutableStateFlow<RefreshStatus>(RefreshStatus.Running)

    val state: StateFlow<PlaylistsUiState> = combine(
        playlistRepository.observeTrending(),
        refreshStatus,
        ::buildState,
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), PlaylistsUiState.Chargement)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            refreshStatus.value = RefreshStatus.Running
            refreshStatus.value = playlistRepository.refreshTrending().toRefreshStatus()
        }
    }

    private fun buildState(playlists: List<Playlist>, status: RefreshStatus): PlaylistsUiState = when {
        playlists.isNotEmpty() -> PlaylistsUiState.Succes(
            playlists = playlists,
            isRefreshing = status is RefreshStatus.Running,
            staleMessage = (status as? RefreshStatus.Failed)?.message,
        )
        status is RefreshStatus.Running -> PlaylistsUiState.Chargement
        status is RefreshStatus.Failed -> PlaylistsUiState.Erreur(status.message)
        else -> PlaylistsUiState.Vide
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L

        val Factory = viewModelFactory {
            initializer { PlaylistsViewModel(spookifyApp().container.playlistRepository) }
        }
    }
}
