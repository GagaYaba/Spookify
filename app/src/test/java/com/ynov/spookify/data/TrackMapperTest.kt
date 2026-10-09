package com.ynov.spookify.data

import com.ynov.spookify.BuildConfig
import com.ynov.spookify.data.mapper.artworkUrl
import com.ynov.spookify.data.mapper.coverUrl
import com.ynov.spookify.data.mapper.toEntities
import com.ynov.spookify.data.mapper.toEntity
import com.ynov.spookify.data.mapper.toTrack
import com.ynov.spookify.data.remote.dto.ImageDto
import com.ynov.spookify.data.remote.dto.TrackDto
import com.ynov.spookify.data.remote.dto.UserDto
import com.ynov.spookify.fakes.trackDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TrackMapperTest {

    @Test
    fun `stream url is built from base url, track id and app name`() {
        val entity = trackDto("abc").toEntity()

        assertEquals("${BuildConfig.API_BASE_URL}tracks/abc/stream?app_name=${BuildConfig.APP_NAME}", entity.streamUrl)
    }

    @Test
    fun `missing fields fall back to readable defaults`() {
        val entity = TrackDto(id = "x").toEntity()

        assertEquals("Titre inconnu", entity.title)
        assertEquals("Artiste inconnu", entity.artistName)
        assertEquals("", entity.artworkUrl)
        assertEquals("", entity.artistId)
    }

    @Test
    fun `artist name falls back to handle when name is missing`() {
        val dto = TrackDto(id = "x", user = UserDto(id = "u", name = null, handle = "djhandle"))

        assertEquals("djhandle", dto.toEntity().artistName)
    }

    @Test
    fun `entries without id or flagged as deleted are dropped`() {
        val dtos = listOf(trackDto("a"), trackDto(""), trackDto("c", deleted = true), trackDto("d"))

        assertEquals(listOf("a", "d"), dtos.toEntities().map { it.id })
    }

    @Test
    fun `entity maps to a domain track keeping the favorite flag`() {
        val track = trackDto("a", duration = 95, streamable = false).toEntity().toTrack(isFavorite = true)

        assertEquals("a", track.id)
        assertEquals(95, track.durationSeconds)
        assertFalse(track.isStreamable)
        assertTrue(track.isFavorite)
    }

    @Test
    fun `artwork picks the medium size first then falls back`() {
        assertEquals("m", ImageDto(small = "s", medium = "m", large = "l").artworkUrl())
        assertEquals("l", ImageDto(small = "s", large = "l").artworkUrl())
        assertEquals("s", ImageDto(small = "s").artworkUrl())
        assertEquals("", (null as ImageDto?).artworkUrl())
    }

    @Test
    fun `cover photo uses the cover keys`() {
        assertEquals("c640", ImageDto(coverMedium = "c640", coverLarge = "c2000").coverUrl())
        assertEquals("c2000", ImageDto(coverLarge = "c2000").coverUrl())
    }
}
