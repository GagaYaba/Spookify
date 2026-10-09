package com.ynov.spookify.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ynov.spookify.data.repository.TrackRepository
import com.ynov.spookify.domain.model.Track
import com.ynov.spookify.player.PlayerController
import com.ynov.spookify.player.PlayerState
import com.ynov.spookify.ui.spookifyApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Porté par l'activité : le mini-player et l'écran lecteur partagent la même instance. */
class PlayerViewModel(
    private val controller: PlayerController,
    private val trackRepository: TrackRepository,
) : ViewModel() {

    // Observé par id de morceau seulement : la position change deux fois par seconde, pas le favori.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val isFavorite: Flow<Boolean> = controller.state
        .map { it.currentTrack?.id }
        .distinctUntilChanged()
        .flatMapLatest { id ->
            if (id == null) flowOf(false) else trackRepository.observeTrack(id).map { it?.isFavorite ?: false }
        }

    val state: StateFlow<PlayerUiState> = combine(controller.state, isFavorite, ::PlayerUiState)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), PlayerUiState(PlayerState(), false))

    fun play(queue: List<Track>, startTrackId: String) = controller.play(queue, startTrackId)

    fun togglePlayPause() = controller.togglePlayPause()

    fun next() = controller.next()

    fun previous() = controller.previous()

    fun seekTo(positionMs: Long) = controller.seekTo(positionMs)

    fun toggleRepeat() = controller.toggleRepeat()

    fun toggleMute() = controller.toggleMute()

    fun dismissError() = controller.dismissError()

    fun toggleFavorite() {
        val current = state.value
        val track = current.player.currentTrack ?: return
        viewModelScope.launch { trackRepository.setFavorite(track.id, !current.isFavorite) }
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L

        val Factory = viewModelFactory {
            initializer {
                val container = spookifyApp().container
                PlayerViewModel(container.playerController, container.trackRepository)
            }
        }
    }
}
