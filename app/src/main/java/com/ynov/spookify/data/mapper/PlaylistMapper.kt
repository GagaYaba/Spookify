package com.ynov.spookify.data.mapper

import com.ynov.spookify.data.local.entity.PlaylistEntity
import com.ynov.spookify.data.remote.dto.PlaylistDto
import com.ynov.spookify.domain.model.Playlist

fun PlaylistDto.toEntity(): PlaylistEntity = PlaylistEntity(
    id = id,
    name = name ?: "Playlist sans titre",
    description = description ?: "",
    artworkUrl = artwork.artworkUrl(),
    ownerName = user?.name ?: user?.handle ?: "",
    trackCount = trackCount,
    totalPlayCount = totalPlayCount,
    trendingRank = null,
)

fun List<PlaylistDto>.toEntities(): List<PlaylistEntity> =
    filter { it.id.isNotBlank() && !it.isDelete }.map(PlaylistDto::toEntity)

fun PlaylistEntity.toPlaylist(): Playlist = Playlist(
    id = id,
    name = name,
    description = description,
    artworkUrl = artworkUrl,
    ownerName = ownerName,
    trackCount = trackCount,
    totalPlayCount = totalPlayCount,
)
