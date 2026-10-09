package com.ynov.spookify.ui.screens.artist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ynov.spookify.R
import com.ynov.spookify.domain.model.Artist
import com.ynov.spookify.ui.components.ArtworkImage
import com.ynov.spookify.ui.components.EmptyView
import com.ynov.spookify.ui.components.ErrorView
import com.ynov.spookify.ui.components.LoadingView
import com.ynov.spookify.ui.components.ScreenHeader
import com.ynov.spookify.ui.components.SectionHeader
import com.ynov.spookify.ui.components.StaleBanner
import com.ynov.spookify.ui.components.TrackActions
import com.ynov.spookify.ui.components.TrackRow
import com.ynov.spookify.ui.util.toCompactCount

@Composable
fun ArtistScreen(
    state: ArtistUiState,
    actions: TrackActions,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        ScreenHeader(title = (state as? ArtistUiState.Succes)?.artist?.name ?: stringResource(R.string.artist_title), onBack = onBack)
        when (state) {
            ArtistUiState.Chargement -> LoadingView()
            ArtistUiState.Vide -> EmptyView(stringResource(R.string.artist_empty))
            is ArtistUiState.Erreur -> ErrorView(state.message, onRetry = onRetry)
            is ArtistUiState.Succes -> LazyColumn(Modifier.fillMaxSize()) {
                state.staleMessage?.let { message -> item { StaleBanner(message) } }
                item { ArtistSummary(state.artist) }
                item { SectionHeader(stringResource(R.string.artist_tracks)) }
                items(state.tracks, key = { it.id }) { track ->
                    TrackRow(
                        track = track,
                        isCurrent = track.id == actions.currentTrackId,
                        onClick = { actions.onPlay(state.tracks, track.id) },
                        onToggleFavorite = { actions.onToggleFavorite(track) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistSummary(artist: Artist) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ArtworkImage(url = artist.avatarUrl, modifier = Modifier.size(96.dp), shape = CircleShape)
        Text(
            text = "@${artist.handle}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(R.string.artist_stats, artist.followerCount.toCompactCount(), artist.trackCount),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
        if (artist.bio.isNotBlank()) {
            Text(
                text = artist.bio,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 4,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
