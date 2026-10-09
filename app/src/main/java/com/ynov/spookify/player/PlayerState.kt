package com.ynov.spookify.player

import com.ynov.spookify.domain.model.Track

data class PlayerState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false,
    val isMuted: Boolean = false,
    val repeatAll: Boolean = false,
    val error: String? = null,
)
