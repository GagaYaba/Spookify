package com.ynov.spookify.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDto(
    val id: String = "",
    @SerialName("playlist_name") val name: String? = null,
    val description: String? = null,
    val artwork: ImageDto? = null,
    val user: UserDto? = null,
    @SerialName("track_count") val trackCount: Int = 0,
    @SerialName("total_play_count") val totalPlayCount: Int = 0,
    @SerialName("is_delete") val isDelete: Boolean = false,
)
