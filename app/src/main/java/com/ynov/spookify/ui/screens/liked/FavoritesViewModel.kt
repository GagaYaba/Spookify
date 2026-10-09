package com.ynov.spookify.ui.screens.liked

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ynov.spookify.data.repository.TrackRepository
import com.ynov.spookify.domain.model.Track
import com.ynov.spookify.ui.spookifyApp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Porté par l'activité : sert à la fois à l'écran Favoris et au bouton cœur des autres écrans. */
class FavoritesViewModel(private val trackRepository: TrackRepository) : ViewModel() {

    val state: StateFlow<FavoritesUiState> = trackRepository.observeFavorites()
        .map { tracks -> if (tracks.isEmpty()) FavoritesUiState.Vide else FavoritesUiState.Succes(tracks) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), FavoritesUiState.Chargement)

    fun toggleFavorite(track: Track) {
        viewModelScope.launch { trackRepository.setFavorite(track.id, !track.isFavorite) }
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L

        val Factory = viewModelFactory {
            initializer { FavoritesViewModel(spookifyApp().container.trackRepository) }
        }
    }
}
