package com.ynov.spookify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val artworkUrl: String,
    val ownerName: String,
    val trackCount: Int,
    val totalPlayCount: Int,
    // Non null = fait partie des tendances, à cette position.
    val trendingRank: Int?,
)
