package com.ynov.spookify.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.ynov.spookify.R

/** Les quatre onglets de la barre du bas, dans l'ordre de la maquette. */
enum class TopLevelDestination(val route: Any, @StringRes val labelRes: Int, val icon: ImageVector) {
    Discover(HomeRoute, R.string.tab_discover, Icons.Outlined.Home),
    Liked(LikedRoute, R.string.tab_liked, Icons.Outlined.FavoriteBorder),
    Playlists(PlaylistsRoute, R.string.tab_playlists, Icons.AutoMirrored.Outlined.QueueMusic),
    Settings(SettingsRoute, R.string.tab_settings, Icons.Outlined.Settings),
}
