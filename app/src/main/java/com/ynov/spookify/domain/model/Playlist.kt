package com.ynov.spookify.domain.model

data class Playlist(
    val id: String,
    val name: String,
    val description: String,
    val artworkUrl: String,
    val ownerName: String,
    val trackCount: Int = 0,
    val totalPlayCount: Int = 0,
)
