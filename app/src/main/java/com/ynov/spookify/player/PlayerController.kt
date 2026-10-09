package com.ynov.spookify.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.ynov.spookify.data.repository.SettingsRepository
import com.ynov.spookify.data.repository.TrackRepository
import com.ynov.spookify.domain.model.Track
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Pont entre l'UI et [PlaybackService]. L'état de lecture (morceau, position, play/pause) vit ici
 * et dans le service, jamais dans un composable.
 */
class PlayerController(
    private val context: Context,
    private val scope: CoroutineScope,
    private val trackRepository: TrackRepository,
    private val settingsRepository: SettingsRepository,
) {
    private val mutableState = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = mutableState.asStateFlow()

    private var controller: MediaController? = null
    private var recordedTrackId: String? = null

    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            publish(player)
            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) recordedTrackId = null
            // On enregistre quand la lecture démarre vraiment, pas quand un item est simplement chargé (restauration).
            if (player.isPlaying) {
                player.currentMediaItem?.mediaId
                    ?.takeIf { it != recordedTrackId }
                    ?.let { id ->
                        recordedTrackId = id
                        onTrackStarted(id)
                    }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            mutableState.update { it.copy(error = error.toUserMessage()) }
        }
    }

    init {
        scope.launch {
            connect()
            // La position avance sans événement du player : on l'échantillonne tant que ça joue.
            while (isActive) {
                delay(POSITION_TICK_MS)
                controller?.takeIf { it.isPlaying }?.let(::publish)
            }
        }
    }

    fun play(queue: List<Track>, startTrackId: String) {
        val target = queue.firstOrNull { it.id == startTrackId }
        if (target != null && !target.isStreamable) {
            reportError("Ce morceau n'est pas diffusable.")
            return
        }
        val active = controller ?: return reportError("Le lecteur démarre, réessayez dans un instant.")
        val playable = queue.filter { it.isStreamable }
        val startIndex = playable.indexOfFirst { it.id == startTrackId }.coerceAtLeast(0)
        active.setMediaItems(playable.map(Track::toMediaItem), startIndex, 0L)
        active.prepare()
        active.play()
    }

    fun togglePlayPause() {
        controller?.let {
            when {
                it.isPlaying -> it.pause()
                // Après une erreur ou une restauration, le player est IDLE : il faut le préparer avant de jouer.
                it.playbackState == Player.STATE_IDLE -> {
                    it.prepare()
                    it.play()
                }
                else -> it.play()
            }
        }
    }

    fun next() {
        controller?.seekToNextMediaItem()
    }

    fun previous() {
        controller?.seekToPreviousMediaItem()
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
    }

    fun toggleRepeat() {
        controller?.let {
            it.repeatMode = if (it.repeatMode == Player.REPEAT_MODE_ALL) Player.REPEAT_MODE_OFF else Player.REPEAT_MODE_ALL
            publish(it)
        }
    }

    fun toggleMute() {
        controller?.let {
            it.volume = if (it.volume == 0f) 1f else 0f
            publish(it)
            scope.launch { settingsRepository.setVolume(it.volume) }
        }
    }

    fun dismissError() {
        mutableState.update { it.copy(error = null) }
    }

    private suspend fun connect() {
        try {
            val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
            val connected = MediaController.Builder(context, token).buildAsync().await()
            connected.volume = settingsRepository.settings.first().volume
            connected.addListener(listener)
            controller = connected
            // Process tué puis relancé : le service est reparti de zéro, on réaffiche le dernier morceau écouté.
            if (connected.mediaItemCount == 0) restoreLastTrack(connected)
            publish(connected)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            reportError("Impossible de démarrer le lecteur audio.")
        }
    }

    private suspend fun restoreLastTrack(player: Player) {
        val trackId = settingsRepository.settings.first().lastTrackId ?: return
        val track = trackRepository.observeTrack(trackId).first() ?: return
        if (track.isStreamable) player.setMediaItem(track.toMediaItem())
    }

    private fun publish(player: Player) {
        val track = player.currentMediaItem?.toTrack()
        val duration = player.duration.takeIf { it != C.TIME_UNSET && it > 0 }
            ?: ((track?.durationSeconds ?: 0) * 1000L)
        mutableState.update {
            it.copy(
                currentTrack = track,
                isPlaying = player.isPlaying,
                isBuffering = player.playbackState == Player.STATE_BUFFERING,
                positionMs = player.currentPosition.coerceAtLeast(0L),
                durationMs = duration,
                hasNext = player.hasNextMediaItem(),
                hasPrevious = player.hasPreviousMediaItem(),
                isMuted = player.volume == 0f,
                repeatAll = player.repeatMode == Player.REPEAT_MODE_ALL,
            )
        }
    }

    private fun onTrackStarted(trackId: String) {
        scope.launch {
            trackRepository.recordPlay(trackId)
            settingsRepository.setLastTrackId(trackId)
        }
    }

    private fun reportError(message: String) {
        mutableState.update { it.copy(error = message) }
    }

    private companion object {
        const val POSITION_TICK_MS = 500L
    }
}
