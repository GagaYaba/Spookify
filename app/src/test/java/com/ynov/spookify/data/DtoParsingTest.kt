package com.ynov.spookify.data

import com.ynov.spookify.data.remote.apiJson
import com.ynov.spookify.data.remote.dto.ApiResponse
import com.ynov.spookify.data.remote.dto.TrackDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DtoParsingTest {

    @Test
    fun `parses a response wrapped in data and ignores unknown keys`() {
        val json = """
            {"data":[{"id":"qbMN0AE","title":"Song","duration":3410,"play_count":2111,
              "genre":"Electronic","unknown_field":{"a":1},
              "artwork":{"150x150":"s","480x480":"m","1000x1000":"l","mirrors":["x"]},
              "user":{"id":"k259kWP","name":"Van Snyder","handle":"vs","follower_count":6252}}]}
        """.trimIndent()

        val track = apiJson.decodeFromString<ApiResponse<List<TrackDto>>>(json).data.orEmpty().single()

        assertEquals("qbMN0AE", track.id)
        assertEquals(3410, track.duration)
        assertEquals("m", track.artwork?.medium)
        assertEquals("Van Snyder", track.user?.name)
        assertEquals(6252, track.user?.followerCount)
    }

    @Test
    fun `null and missing fields use defaults instead of failing`() {
        val json = """{"data":[{"id":"a","title":null,"duration":null,"is_streamable":null}]}"""

        val track = apiJson.decodeFromString<ApiResponse<List<TrackDto>>>(json).data.orEmpty().single()

        assertNull(track.title)
        assertEquals(0, track.duration)
        assertTrue(track.isStreamable)
        assertNull(track.artwork)
    }

    @Test
    fun `empty data array is parsed as an empty list`() {
        val response = apiJson.decodeFromString<ApiResponse<List<TrackDto>>>("""{"data":[]}""")

        assertNotNull(response.data)
        assertTrue(response.data.orEmpty().isEmpty())
    }

    @Test
    fun `response without data parses as null data`() {
        assertNull(apiJson.decodeFromString<ApiResponse<List<TrackDto>>>("{}").data)
    }
}
