package com.ynov.spookify.ui.screens.playlists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ynov.spookify.R
import com.ynov.spookify.ui.components.EmptyView
import com.ynov.spookify.ui.components.ErrorView
import com.ynov.spookify.ui.components.LoadingView
import com.ynov.spookify.ui.components.PlaylistCard
import com.ynov.spookify.ui.components.ScreenHeader
import com.ynov.spookify.ui.components.StaleBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    state: PlaylistsUiState,
    onRefresh: () -> Unit,
    onPlaylistClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        ScreenHeader(title = stringResource(R.string.playlists_title))
        when (state) {
            PlaylistsUiState.Chargement -> LoadingView()
            PlaylistsUiState.Vide -> EmptyView(stringResource(R.string.playlists_empty))
            is PlaylistsUiState.Erreur -> ErrorView(state.message, onRetry = onRefresh)
            is PlaylistsUiState.Succes -> PullToRefreshBox(isRefreshing = state.isRefreshing, onRefresh = onRefresh) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    state.staleMessage?.let { message ->
                        item(span = { GridItemSpan(maxLineSpan) }) { StaleBanner(message) }
                    }
                    items(state.playlists, key = { it.id }) { playlist ->
                        PlaylistCard(playlist = playlist, onClick = { onPlaylistClick(playlist.id) })
                    }
                }
            }
        }
    }
}
