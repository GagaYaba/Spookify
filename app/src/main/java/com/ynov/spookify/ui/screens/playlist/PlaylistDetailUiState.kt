package com.ynov.spookify.ui.screens.playlist

import com.ynov.spookify.domain.model.Playlist
import com.ynov.spookify.domain.model.Track

sealed class PlaylistDetailUiState {
    data object Chargement : PlaylistDetailUiState()
    data object Vide : PlaylistDetailUiState()
    data class Erreur(val message: String) : PlaylistDetailUiState()
    data class Succes(
        val playlist: Playlist,
        val tracks: List<Track>,
        val staleMessage: String? = null,
    ) : PlaylistDetailUiState()
}
