package com.ynov.spookify.player

import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.ynov.spookify.domain.model.Track

private const val EXTRA_ARTIST_ID = "artistId"
private const val EXTRA_DURATION = "durationSeconds"
private const val EXTRA_ARTWORK_LARGE = "artworkLargeUrl"

fun Track.toMediaItem(): MediaItem = MediaItem.Builder()
    .setMediaId(id)
    .setUri(streamUrl)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artistName)
            .setArtworkUri(artworkUrl.takeIf { it.isNotBlank() }?.toUri())
            .setExtras(bundleOf(EXTRA_ARTIST_ID to artistId, EXTRA_DURATION to durationSeconds, EXTRA_ARTWORK_LARGE to artworkLargeUrl))
            .build()
    )
    .build()

/** Le service peut survivre à l'activité : on reconstruit le morceau depuis les métadonnées de l'item. */
fun MediaItem.toTrack(): Track = Track(
    id = mediaId,
    title = mediaMetadata.title?.toString() ?: "",
    artistId = mediaMetadata.extras?.getString(EXTRA_ARTIST_ID) ?: "",
    artistName = mediaMetadata.artist?.toString() ?: "",
    artworkUrl = mediaMetadata.artworkUri?.toString() ?: "",
    artworkLargeUrl = mediaMetadata.extras?.getString(EXTRA_ARTWORK_LARGE) ?: "",
    streamUrl = localConfiguration?.uri?.toString() ?: "",
    durationSeconds = mediaMetadata.extras?.getInt(EXTRA_DURATION) ?: 0,
)
