package com.ynov.spookify.data.mapper

import com.ynov.spookify.BuildConfig
import com.ynov.spookify.data.local.entity.TrackEntity
import com.ynov.spookify.data.local.entity.TrackRow
import com.ynov.spookify.data.remote.dto.TrackDto
import com.ynov.spookify.domain.model.Track

// L'URL de stream est construite plutôt que lue dans la réponse : elle redirige vers un nœud valide à chaque lecture.
private fun streamUrlFor(trackId: String): String =
    "${BuildConfig.API_BASE_URL}tracks/$trackId/stream?app_name=${BuildConfig.APP_NAME}"

fun TrackDto.toEntity(): TrackEntity = TrackEntity(
    id = id,
    title = title ?: "Titre inconnu",
    artistId = user?.id ?: "",
    artistName = user?.name ?: user?.handle ?: "Artiste inconnu",
    artistAvatarUrl = user?.profilePicture.artworkUrl(),
    artworkUrl = artwork.artworkUrl(),
    artworkLargeUrl = artwork.largeArtworkUrl(),
    streamUrl = streamUrlFor(id),
    durationSeconds = duration,
    playCount = playCount,
    genre = genre ?: "",
    isStreamable = isStreamable,
)

/** Ignore les entrées sans id ou supprimées, que l'API laisse parfois passer. */
fun List<TrackDto>.toEntities(): List<TrackEntity> =
    filter { it.id.isNotBlank() && !it.isDelete }.map(TrackDto::toEntity)

fun TrackEntity.toTrack(isFavorite: Boolean = false): Track = Track(
    id = id,
    title = title,
    artistId = artistId,
    artistName = artistName,
    artistAvatarUrl = artistAvatarUrl,
    artworkUrl = artworkUrl,
    artworkLargeUrl = artworkLargeUrl,
    streamUrl = streamUrl,
    durationSeconds = durationSeconds,
    playCount = playCount,
    genre = genre,
    isStreamable = isStreamable,
    isFavorite = isFavorite,
)

fun TrackRow.toTrack(): Track = track.toTrack(isFavorite)
