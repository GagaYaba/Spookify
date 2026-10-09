package com.ynov.spookify.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ynov.spookify.R
import com.ynov.spookify.domain.model.Artist
import com.ynov.spookify.domain.model.Playlist
import com.ynov.spookify.domain.model.Track

@Composable
private fun SoftCard(onClick: () -> Unit, modifier: Modifier, content: @Composable () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        content()
    }
}

@Composable
fun PlaylistCard(playlist: Playlist, onClick: () -> Unit, modifier: Modifier = Modifier) {
    SoftCard(onClick, modifier) {
        Column(Modifier.padding(8.dp)) {
            ArtworkImage(
                url = playlist.artworkUrl,
                modifier = Modifier.fillMaxWidth().aspectRatio(1.7f),
                shape = MaterialTheme.shapes.small,
            )
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = stringResource(R.string.playlist_subtitle, playlist.ownerName, playlist.trackCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun ArtistCard(artist: Artist, onClick: () -> Unit, modifier: Modifier = Modifier) {
    SoftCard(onClick, modifier) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ArtworkImage(url = artist.avatarUrl, modifier = Modifier.size(44.dp), shape = CircleShape)
            Text(
                text = artist.name,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
fun SongCard(track: Track, onClick: () -> Unit, modifier: Modifier = Modifier) {
    SoftCard(onClick, modifier) {
        Column(Modifier.padding(8.dp)) {
            ArtworkImage(
                url = track.artworkUrl,
                modifier = Modifier.fillMaxWidth().aspectRatio(1.3f),
                shape = MaterialTheme.shapes.small,
            )
            Text(
                text = track.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = track.artistName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
