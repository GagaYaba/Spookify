package com.ynov.spookify.player

import androidx.media3.common.PlaybackException

/** Traduit l'erreur ExoPlayer en message lisible : réseau coupé et morceau indisponible se corrigent différemment. */
internal fun PlaybackException.toUserMessage(): String = when (errorCode) {
    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "Pas de connexion internet."

    PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
    PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> "Ce morceau n'est pas disponible pour le moment."

    else -> "Lecture impossible. Réessayez."
}
