package com.ynov.spookify.player

import androidx.media3.common.PlaybackException
import kotlin.test.Test
import kotlin.test.assertEquals

class PlaybackErrorsTest {

    private fun error(code: Int) = PlaybackException("test", null, code)

    @Test
    fun `network errors ask to check the connection`() {
        assertEquals("Pas de connexion internet.", error(PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED).toUserMessage())
        assertEquals("Pas de connexion internet.", error(PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT).toUserMessage())
    }

    @Test
    fun `server refusals say the track is unavailable`() {
        val message = "Ce morceau n'est pas disponible pour le moment."
        assertEquals(message, error(PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS).toUserMessage())
        assertEquals(message, error(PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND).toUserMessage())
    }

    @Test
    fun `other errors get a generic retry message`() {
        assertEquals("Lecture impossible. Réessayez.", error(PlaybackException.ERROR_CODE_DECODING_FAILED).toUserMessage())
    }
}
