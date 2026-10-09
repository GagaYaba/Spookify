package com.ynov.spookify.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String = "",
    val name: String? = null,
    val handle: String? = null,
    @SerialName("profile_picture") val profilePicture: ImageDto? = null,
    @SerialName("cover_photo") val coverPhoto: ImageDto? = null,
    @SerialName("follower_count") val followerCount: Int = 0,
    @SerialName("track_count") val trackCount: Int = 0,
    val bio: String? = null,
)
