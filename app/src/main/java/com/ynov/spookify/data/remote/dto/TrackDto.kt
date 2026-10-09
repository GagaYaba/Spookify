package com.ynov.spookify.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackDto(
    val id: String = "",
    val title: String? = null,
    val duration: Int = 0,
    @SerialName("play_count") val playCount: Int = 0,
    val genre: String? = null,
    val artwork: ImageDto? = null,
    val user: UserDto? = null,
    @SerialName("is_streamable") val isStreamable: Boolean = true,
    @SerialName("is_delete") val isDelete: Boolean = false,
)
