package com.ynov.spookify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ynov.spookify.domain.model.ThemeMode
import com.ynov.spookify.ui.components.TrackActions
import com.ynov.spookify.ui.screens.artist.ArtistScreen
import com.ynov.spookify.ui.screens.artist.ArtistViewModel
import com.ynov.spookify.ui.screens.home.HomeScreen
import com.ynov.spookify.ui.screens.home.HomeViewModel
import com.ynov.spookify.ui.screens.liked.FavoritesUiState
import com.ynov.spookify.ui.screens.liked.LikedScreen
import com.ynov.spookify.ui.screens.player.PlayerScreen
import com.ynov.spookify.ui.screens.player.PlayerUiState
import com.ynov.spookify.ui.screens.player.PlayerViewModel
import com.ynov.spookify.ui.screens.playlist.PlaylistDetailScreen
import com.ynov.spookify.ui.screens.playlist.PlaylistDetailViewModel
import com.ynov.spookify.ui.screens.playlists.PlaylistsScreen
import com.ynov.spookify.ui.screens.playlists.PlaylistsViewModel
import com.ynov.spookify.ui.screens.search.SearchScreen
import com.ynov.spookify.ui.screens.search.SearchViewModel
import com.ynov.spookify.ui.screens.settings.SettingsScreen

/** Branche chaque route sur son ViewModel ; les écrans eux-mêmes restent sans état. */
@Composable
fun AppNavHost(
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
    favoritesState: FavoritesUiState,
    trackActions: TrackActions,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(navController = navController, startDestination = HomeRoute, modifier = modifier) {
        composable<HomeRoute> {
            val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
            val state by viewModel.state.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                actions = trackActions,
                onRefresh = viewModel::refresh,
                onSearchClick = { navController.navigate(SearchRoute) },
                onSeeAllPlaylists = { navController.navigateToTopLevel(TopLevelDestination.Playlists) },
                onPlaylistClick = { navController.navigate(PlaylistDetailRoute(it)) },
                onArtistClick = { navController.navigate(ArtistDetailRoute(it)) },
            )
        }
        composable<SearchRoute> {
            val viewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory)
            val query by viewModel.query.collectAsStateWithLifecycle()
            val state by viewModel.state.collectAsStateWithLifecycle()
            SearchScreen(
                query = query,
                state = state,
                actions = trackActions,
                onQueryChange = viewModel::onQueryChange,
                onRetry = viewModel::retry,
                onBack = navController::popBackStack,
            )
        }
        composable<LikedRoute> {
            LikedScreen(state = favoritesState, actions = trackActions)
        }
        composable<PlaylistsRoute> {
            val viewModel: PlaylistsViewModel = viewModel(factory = PlaylistsViewModel.Factory)
            val state by viewModel.state.collectAsStateWithLifecycle()
            PlaylistsScreen(
                state = state,
                onRefresh = viewModel::refresh,
                onPlaylistClick = { navController.navigate(PlaylistDetailRoute(it)) },
            )
        }
        composable<SettingsRoute> {
            SettingsScreen(themeMode = themeMode, onThemeChange = onThemeChange)
        }
        composable<PlaylistDetailRoute> {
            val viewModel: PlaylistDetailViewModel = viewModel(factory = PlaylistDetailViewModel.Factory)
            val state by viewModel.state.collectAsStateWithLifecycle()
            PlaylistDetailScreen(
                state = state,
                actions = trackActions,
                onRetry = viewModel::refresh,
                onBack = navController::popBackStack,
            )
        }
        composable<ArtistDetailRoute> {
            val viewModel: ArtistViewModel = viewModel(factory = ArtistViewModel.Factory)
            val state by viewModel.state.collectAsStateWithLifecycle()
            ArtistScreen(
                state = state,
                actions = trackActions,
                onRetry = viewModel::refresh,
                onBack = navController::popBackStack,
            )
        }
        composable<PlayerRoute> {
            val state: PlayerUiState by playerViewModel.state.collectAsStateWithLifecycle()
            PlayerScreen(
                state = state,
                onBack = navController::popBackStack,
                onPlayPause = playerViewModel::togglePlayPause,
                onNext = playerViewModel::next,
                onPrevious = playerViewModel::previous,
                onSeek = playerViewModel::seekTo,
                onToggleFavorite = playerViewModel::toggleFavorite,
                onToggleMute = playerViewModel::toggleMute,
                onToggleRepeat = playerViewModel::toggleRepeat,
                onArtistClick = { navController.navigate(ArtistDetailRoute(it)) },
            )
        }
    }
}
