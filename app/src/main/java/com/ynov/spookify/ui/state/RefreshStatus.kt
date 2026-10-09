package com.ynov.spookify.ui.state

/** Où en est le rafraîchissement réseau, indépendamment de ce que le cache Room affiche déjà. */
sealed class RefreshStatus {
    data object Running : RefreshStatus()
    data object Idle : RefreshStatus()
    data class Failed(val message: String) : RefreshStatus()
}

private const val DEFAULT_ERROR = "Une erreur est survenue."

fun List<Result<Unit>>.toRefreshStatus(): RefreshStatus =
    firstNotNullOfOrNull { it.exceptionOrNull() }
        ?.let { RefreshStatus.Failed(it.message ?: DEFAULT_ERROR) }
        ?: RefreshStatus.Idle

fun Result<Unit>.toRefreshStatus(): RefreshStatus = listOf(this).toRefreshStatus()
