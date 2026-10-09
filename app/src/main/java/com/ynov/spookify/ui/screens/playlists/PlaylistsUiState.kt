package com.ynov.spookify.ui.screens.playlists

import com.ynov.spookify.domain.model.Playlist

sealed class PlaylistsUiState {
    data object Chargement : PlaylistsUiState()
    data object Vide : PlaylistsUiState()
    data class Erreur(val message: String) : PlaylistsUiState()
    data class Succes(
        val playlists: List<Playlist>,
        val isRefreshing: Boolean = false,
        val staleMessage: String? = null,
    ) : PlaylistsUiState()
}
