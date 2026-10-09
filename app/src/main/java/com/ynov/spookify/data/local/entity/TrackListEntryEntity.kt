package com.ynov.spookify.data.local.entity

import androidx.room.Entity
import androidx.room.Index

// Une "liste" (tendances, recherche, playlist, artiste) est un ensemble ordonné d'ids rattaché à une clé.
@Entity(
    tableName = "track_list_entries",
    primaryKeys = ["listKey", "trackId"],
    indices = [Index("trackId")],
)
data class TrackListEntryEntity(
    val listKey: String,
    val trackId: String,
    val position: Int,
)
