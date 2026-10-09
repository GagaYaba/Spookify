package com.ynov.spookify.domain.model

data class Track(
    val id: String,
    val title: String,
    val artistId: String,
    val artistName: String,
    val artistAvatarUrl: String = "",
    val artworkUrl: String,
    val artworkLargeUrl: String = "",
    val streamUrl: String,
    val durationSeconds: Int,
    val playCount: Int = 0,
    val genre: String = "",
    val isStreamable: Boolean = true,
    val isFavorite: Boolean = false,
)

/** Résumé d'artiste déduit d'un morceau : suffit pour afficher une pastille sans appel réseau. */
fun Track.toArtistSummary(): Artist = Artist(
    id = artistId,
    name = artistName,
    handle = "",
    avatarUrl = artistAvatarUrl,
    coverUrl = "",
)
