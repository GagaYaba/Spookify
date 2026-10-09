package com.ynov.spookify.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ynov.spookify.R
import com.ynov.spookify.ui.components.ArtistCard
import com.ynov.spookify.ui.components.EmptyView
import com.ynov.spookify.ui.components.ErrorView
import com.ynov.spookify.ui.components.LoadingView
import com.ynov.spookify.ui.components.PlaylistCard
import com.ynov.spookify.ui.components.ScreenHeader
import com.ynov.spookify.ui.components.SectionHeader
import com.ynov.spookify.ui.components.SongCard
import com.ynov.spookify.ui.components.StaleBanner
import com.ynov.spookify.ui.components.TrackActions
import com.ynov.spookify.ui.components.TrackRow
import com.ynov.spookify.ui.util.currentGreeting

@Composable
fun HomeScreen(
    state: HomeUiState,
    actions: TrackActions,
    onRefresh: () -> Unit,
    onSearchClick: () -> Unit,
    onSeeAllPlaylists: () -> Unit,
    onPlaylistClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val greeting = remember { currentGreeting() }
    Column(modifier.fillMaxSize()) {
        ScreenHeader(title = stringResource(greeting.titleRes)) {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.home_search))
            }
        }
        when (state) {
            HomeUiState.Chargement -> LoadingView()
            HomeUiState.Vide -> EmptyView(stringResource(R.string.home_empty))
            is HomeUiState.Erreur -> ErrorView(state.message, onRetry = onRefresh)
            is HomeUiState.Succes -> HomeContent(
                state = state,
                actions = actions,
                onRefresh = onRefresh,
                onSeeAllPlaylists = onSeeAllPlaylists,
                onPlaylistClick = onPlaylistClick,
                onArtistClick = onArtistClick,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    state: HomeUiState.Succes,
    actions: TrackActions,
    onRefresh: () -> Unit,
    onSeeAllPlaylists: () -> Unit,
    onPlaylistClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
) {
    PullToRefreshBox(isRefreshing = state.isRefreshing, onRefresh = onRefresh) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
            state.staleMessage?.let { message -> item { StaleBanner(message) } }

            if (state.playlists.isNotEmpty()) {
                item { SectionHeader(stringResource(R.string.home_for_you), onSeeAll = onSeeAllPlaylists) }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.playlists, key = { it.id }) { playlist ->
                            PlaylistCard(
                                playlist = playlist,
                                onClick = { onPlaylistClick(playlist.id) },
                                modifier = Modifier.width(220.dp),
                            )
                        }
                    }
                }
            }

            if (state.artists.isNotEmpty()) {
                item { SectionHeader(stringResource(R.string.home_popular_artists)) }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.artists, key = { it.id }) { artist ->
                            ArtistCard(
                                artist = artist,
                                onClick = { onArtistClick(artist.id) },
                                modifier = Modifier.width(96.dp),
                            )
                        }
                    }
                }
            }

            if (state.tracks.isNotEmpty()) {
                item { SectionHeader(stringResource(R.string.home_trending_tracks)) }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.tracks, key = { it.id }) { track ->
                            SongCard(
                                track = track,
                                onClick = { actions.onPlay(state.tracks, track.id) },
                                modifier = Modifier.width(160.dp),
                            )
                        }
                    }
                }
                item { SectionHeader(stringResource(R.string.home_all_tracks)) }
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
