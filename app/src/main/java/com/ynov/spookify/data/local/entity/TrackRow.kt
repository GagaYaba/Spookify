package com.ynov.spookify.data.local.entity

import androidx.room.Embedded

/** Résultat de jointure : un morceau et son statut favori. */
data class TrackRow(
    @Embedded val track: TrackEntity,
    val isFavorite: Boolean,
)
