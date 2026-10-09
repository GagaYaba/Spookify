package com.ynov.spookify.ui.screens.home

import com.ynov.spookify.domain.model.Artist
import com.ynov.spookify.domain.model.Playlist
import com.ynov.spookify.domain.model.Track

sealed class HomeUiState {
    data object Chargement : HomeUiState()
    data object Vide : HomeUiState()
    data class Erreur(val message: String) : HomeUiState()
    data class Succes(
        val playlists: List<Playlist>,
        val artists: List<Artist>,
        val tracks: List<Track>,
        val isRefreshing: Boolean = false,
        // Rafraîchissement raté alors que le cache est affiché (ex. hors-ligne).
        val staleMessage: String? = null,
    ) : HomeUiState()
}
