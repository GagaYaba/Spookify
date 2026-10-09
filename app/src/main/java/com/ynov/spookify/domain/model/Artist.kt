package com.ynov.spookify.domain.model

data class Artist(
    val id: String,
    val name: String,
    val handle: String,
    val avatarUrl: String,
    val coverUrl: String,
    val followerCount: Int = 0,
    val trackCount: Int = 0,
    val bio: String = "",
)
