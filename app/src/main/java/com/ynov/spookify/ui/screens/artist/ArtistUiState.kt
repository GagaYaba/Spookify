package com.ynov.spookify.ui.screens.artist

import com.ynov.spookify.domain.model.Artist
import com.ynov.spookify.domain.model.Track

sealed class ArtistUiState {
    data object Chargement : ArtistUiState()
    data object Vide : ArtistUiState()
    data class Erreur(val message: String) : ArtistUiState()
    data class Succes(
        val artist: Artist,
        val tracks: List<Track>,
        val staleMessage: String? = null,
    ) : ArtistUiState()
}
