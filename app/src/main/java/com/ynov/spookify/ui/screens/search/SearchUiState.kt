package com.ynov.spookify.ui.screens.search

import com.ynov.spookify.domain.model.Track

sealed class SearchUiState {
    data object Chargement : SearchUiState()
    data object Vide : SearchUiState()
    data class Erreur(val message: String) : SearchUiState()
    data class Succes(val tracks: List<Track>) : SearchUiState()
}
