package com.ynov.spookify.domain.model

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val lastTrackId: String? = null,
    val volume: Float = 1f,
)
