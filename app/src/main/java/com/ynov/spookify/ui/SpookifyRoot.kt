package com.ynov.spookify.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ynov.spookify.domain.model.ThemeMode
import com.ynov.spookify.navigation.AppNavHost
import com.ynov.spookify.navigation.PlayerRoute
import com.ynov.spookify.navigation.TopLevelDestination
import com.ynov.spookify.navigation.navigateToTopLevel
import com.ynov.spookify.ui.components.MiniPlayer
import com.ynov.spookify.ui.components.SpookifyBottomBar
import com.ynov.spookify.ui.components.TrackActions
import com.ynov.spookify.ui.components.rememberNotificationPermissionRequest
import com.ynov.spookify.ui.screens.liked.FavoritesViewModel
import com.ynov.spookify.ui.screens.player.PlayerViewModel

@Composable
fun SpookifyRoot(
    playerViewModel: PlayerViewModel,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
) {
    val navController = rememberNavController()
    val favoritesViewModel: FavoritesViewModel = viewModel(factory = FavoritesViewModel.Factory)
    val favoritesState by favoritesViewModel.state.collectAsStateWithLifecycle()
    val playerState by playerViewModel.state.collectAsStateWithLifecycle()

    val destination = navController.currentBackStackEntryAsState().value?.destination
    val isPlayerScreen = destination?.hasRoute<PlayerRoute>() == true
    val selectedTab = TopLevelDestination.entries.firstOrNull { tab ->
        destination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val error = playerState.player.error
    LaunchedEffect(error) {
        if (error != null) {
            snackbarHostState.showSnackbar(error)
            playerViewModel.dismissError()
        }
    }

    val requestNotificationPermission = rememberNotificationPermissionRequest()
    val trackActions = remember(playerState.player.currentTrack?.id) {
        TrackActions(
            currentTrackId = playerState.player.currentTrack?.id,
            onPlay = { queue, trackId ->
                requestNotificationPermission()
                playerViewModel.play(queue, trackId)
            },
            onToggleFavorite = favoritesViewModel::toggleFavorite,
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        // Le lecteur plein écran dessine sous les barres système.
        contentWindowInsets = if (isPlayerScreen) WindowInsets(0) else ScaffoldDefaults.contentWindowInsets,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!isPlayerScreen) {
                Column {
                    MiniPlayer(
                        state = playerState,
                        onClick = { navController.navigate(PlayerRoute) { launchSingleTop = true } },
                        onPlayPause = playerViewModel::togglePlayPause,
                        onToggleMute = playerViewModel::toggleMute,
                    )
                    SpookifyBottomBar(selected = selectedTab, onSelect = navController::navigateToTopLevel)
                }
            }
        },
    ) { padding ->
        AppNavHost(
            navController = navController,
            playerViewModel = playerViewModel,
            favoritesState = favoritesState,
            trackActions = trackActions,
            themeMode = themeMode,
            onThemeChange = onThemeChange,
            modifier = Modifier.padding(padding),
        )
    }
}
