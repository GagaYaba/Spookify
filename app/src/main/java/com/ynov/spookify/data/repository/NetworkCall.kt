package com.ynov.spookify.data.repository

import com.ynov.spookify.domain.model.AppException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/** Exécute un appel réseau et traduit toute erreur en [AppException] au message lisible. */
internal suspend fun <T> apiCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e.toAppException())
}

internal fun Throwable.toAppException(): AppException = when (this) {
    is AppException -> this
    is SocketTimeoutException -> AppException("Le serveur met trop de temps à répondre. Réessayez.", this)
    is HttpException -> AppException(code().toHttpMessage(), this)
    is SerializationException -> AppException("Réponse inattendue du serveur.", this)
    is IOException -> AppException("Pas de connexion internet.", this)
    else -> AppException("Une erreur est survenue.", this)
}

private fun Int.toHttpMessage(): String = when (this) {
    404 -> "Contenu introuvable."
    429 -> "Trop de requêtes, patientez un instant."
    in 400..499 -> "Requête refusée par le serveur ($this)."
    else -> "Le serveur rencontre un problème ($this)."
}
