package com.ynov.spookify.fakes

import com.ynov.spookify.data.remote.dto.ImageDto
import com.ynov.spookify.data.remote.dto.PlaylistDto
import com.ynov.spookify.data.remote.dto.TrackDto
import com.ynov.spookify.data.remote.dto.UserDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher

fun trackDto(
    id: String,
    title: String? = "Titre $id",
    userId: String = "artist-1",
    userName: String? = "Artiste $userId",
    duration: Int = 120,
    streamable: Boolean = true,
    deleted: Boolean = false,
) = TrackDto(
    id = id,
    title = title,
    duration = duration,
    artwork = ImageDto(medium = "https://img.test/$id.jpg"),
    user = UserDto(id = userId, name = userName, profilePicture = ImageDto(medium = "https://img.test/$userId.jpg")),
    isStreamable = streamable,
    isDelete = deleted,
)

fun playlistDto(id: String, name: String? = "Playlist $id", deleted: Boolean = false) = PlaylistDto(
    id = id,
    name = name,
    artwork = ImageDto(medium = "https://img.test/$id.jpg"),
    user = UserDto(id = "owner", name = "Owner"),
    trackCount = 3,
    isDelete = deleted,
)

/** Un StateFlow construit avec stateIn(WhileSubscribed) ne démarre que s'il a un abonné : on en fournit un. */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> TestScope.keepCollecting(flow: StateFlow<T>) {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { flow.collect { } }
}
