package com.ynov.spookify.data

import com.ynov.spookify.data.repository.apiCall
import com.ynov.spookify.domain.model.AppException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.fail

class NetworkCallTest {

    private suspend fun messageOf(error: Throwable): String? {
        val result = apiCall<Unit> { throw error }
        assertTrue(result.isFailure)
        return result.exceptionOrNull()?.also { assertIs<AppException>(it) }?.message
    }

    private fun http(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))

    @Test
    fun `success is wrapped as is`() = runTest {
        assertEquals(42, apiCall { 42 }.getOrNull())
    }

    @Test
    fun `no network becomes a readable message`() = runTest {
        assertEquals("Pas de connexion internet.", messageOf(UnknownHostException()))
        assertEquals("Pas de connexion internet.", messageOf(IOException()))
    }

    @Test
    fun `timeout is distinguished from a generic io error`() = runTest {
        assertEquals("Le serveur met trop de temps à répondre. Réessayez.", messageOf(SocketTimeoutException()))
    }

    @Test
    fun `http errors are translated by status`() = runTest {
        assertEquals("Contenu introuvable.", messageOf(http(404)))
        assertEquals("Trop de requêtes, patientez un instant.", messageOf(http(429)))
        assertEquals("Requête refusée par le serveur (403).", messageOf(http(403)))
        assertEquals("Le serveur rencontre un problème (503).", messageOf(http(503)))
    }

    @Test
    fun `malformed body is reported as an unexpected response`() = runTest {
        assertEquals("Réponse inattendue du serveur.", messageOf(SerializationException("bad json")))
    }

    @Test
    fun `unknown errors get a generic message`() = runTest {
        assertEquals("Une erreur est survenue.", messageOf(IllegalStateException("boom")))
    }

    @Test
    fun `cancellation is rethrown, never swallowed`() = runTest {
        try {
            apiCall<Unit> { throw CancellationException("cancelled") }
            fail("La cancellation aurait dû être relancée")
        } catch (expected: CancellationException) {
            assertEquals("cancelled", expected.message)
        }
    }
}
