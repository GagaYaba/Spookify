package com.ynov.spookify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artistId: String,
    val artistName: String,
    val artistAvatarUrl: String,
    val artworkUrl: String,
    val artworkLargeUrl: String,
    val streamUrl: String,
    val durationSeconds: Int,
    val playCount: Int,
    val genre: String,
    val isStreamable: Boolean,
)
