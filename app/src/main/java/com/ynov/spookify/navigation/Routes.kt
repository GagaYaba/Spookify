package com.ynov.spookify.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object SearchRoute

@Serializable
data object LikedRoute

@Serializable
data object PlaylistsRoute

@Serializable
data object SettingsRoute

@Serializable
data class PlaylistDetailRoute(val id: String)

@Serializable
data class ArtistDetailRoute(val id: String)

@Serializable
data object PlayerRoute
