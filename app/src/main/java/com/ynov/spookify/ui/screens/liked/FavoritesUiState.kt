package com.ynov.spookify.ui.screens.liked

import com.ynov.spookify.domain.model.Track

// Les favoris sont purement locaux (Room) : pas d'état d'erreur réseau.
sealed class FavoritesUiState {
    data object Chargement : FavoritesUiState()
    data object Vide : FavoritesUiState()
    data class Succes(val tracks: List<Track>) : FavoritesUiState()
}
