package com.ynov.spookify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val handle: String,
    val avatarUrl: String,
    val coverUrl: String,
    val followerCount: Int,
    val trackCount: Int,
    val bio: String,
)
