package com.ynov.spookify.ui.screens.playlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ynov.spookify.R
import com.ynov.spookify.domain.model.Playlist
import com.ynov.spookify.ui.components.ArtworkImage
import com.ynov.spookify.ui.components.EmptyView
import com.ynov.spookify.ui.components.ErrorView
import com.ynov.spookify.ui.components.LoadingView
import com.ynov.spookify.ui.components.ScreenHeader
import com.ynov.spookify.ui.components.StaleBanner
import com.ynov.spookify.ui.components.TrackActions
import com.ynov.spookify.ui.components.TrackRow

@Composable
fun PlaylistDetailScreen(
    state: PlaylistDetailUiState,
    actions: TrackActions,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        ScreenHeader(title = (state as? PlaylistDetailUiState.Succes)?.playlist?.name ?: stringResource(R.string.playlist_title), onBack = onBack)
        when (state) {
            PlaylistDetailUiState.Chargement -> LoadingView()
            PlaylistDetailUiState.Vide -> EmptyView(stringResource(R.string.playlist_empty))
            is PlaylistDetailUiState.Erreur -> ErrorView(state.message, onRetry = onRetry)
            is PlaylistDetailUiState.Succes -> LazyColumn(Modifier.fillMaxSize()) {
                state.staleMessage?.let { message -> item { StaleBanner(message) } }
                item {
                    PlaylistSummary(
                        playlist = state.playlist,
                        onPlayAll = { state.tracks.firstOrNull()?.let { actions.onPlay(state.tracks, it.id) } },
                    )
                }
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
private fun PlaylistSummary(playlist: Playlist, onPlayAll: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ArtworkImage(
            url = playlist.artworkUrl,
            modifier = Modifier.size(180.dp),
            shape = MaterialTheme.shapes.medium,
        )
        Text(
            text = playlist.ownerName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp),
        )
        if (playlist.description.isNotBlank()) {
            Text(
                text = playlist.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Button(onClick = onPlayAll, modifier = Modifier.padding(top = 12.dp)) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null)
            Text(stringResource(R.string.play_all))
        }
    }
}
