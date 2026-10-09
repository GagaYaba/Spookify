package com.ynov.spookify.ui.screens.player

import com.ynov.spookify.player.PlayerState

/** État du lecteur enrichi du statut favori, qui vient de Room et non du service. */
data class PlayerUiState(
    val player: PlayerState,
    val isFavorite: Boolean,
)
