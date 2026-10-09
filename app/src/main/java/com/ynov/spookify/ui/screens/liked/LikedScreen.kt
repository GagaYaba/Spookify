package com.ynov.spookify.ui.screens.liked

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ynov.spookify.R
import com.ynov.spookify.ui.components.EmptyView
import com.ynov.spookify.ui.components.LoadingView
import com.ynov.spookify.ui.components.ScreenHeader
import com.ynov.spookify.ui.components.TrackActions
import com.ynov.spookify.ui.components.TrackRow

@Composable
fun LikedScreen(
    state: FavoritesUiState,
    actions: TrackActions,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        ScreenHeader(title = stringResource(R.string.liked_title))
        when (state) {
            FavoritesUiState.Chargement -> LoadingView()
            FavoritesUiState.Vide -> EmptyView(stringResource(R.string.liked_empty))
            is FavoritesUiState.Succes -> LazyColumn(Modifier.fillMaxSize()) {
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
