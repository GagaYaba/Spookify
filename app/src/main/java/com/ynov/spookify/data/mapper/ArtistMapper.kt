package com.ynov.spookify.data.mapper

import com.ynov.spookify.data.local.entity.ArtistEntity
import com.ynov.spookify.data.remote.dto.UserDto
import com.ynov.spookify.domain.model.Artist

fun UserDto.toEntity(): ArtistEntity = ArtistEntity(
    id = id,
    name = name ?: handle ?: "Artiste inconnu",
    handle = handle ?: "",
    avatarUrl = profilePicture.artworkUrl(),
    coverUrl = coverPhoto.coverUrl(),
    followerCount = followerCount,
    trackCount = trackCount,
    bio = bio ?: "",
)

fun ArtistEntity.toArtist(): Artist = Artist(
    id = id,
    name = name,
    handle = handle,
    avatarUrl = avatarUrl,
    coverUrl = coverUrl,
    followerCount = followerCount,
    trackCount = trackCount,
    bio = bio,
)
