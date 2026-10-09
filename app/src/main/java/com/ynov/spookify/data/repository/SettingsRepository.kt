package com.ynov.spookify.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ynov.spookify.domain.model.ThemeMode
import com.ynov.spookify.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    val settings: Flow<UserSettings> = dataStore.data
        // Un fichier illisible ne doit pas bloquer l'app : on retombe sur les valeurs par défaut.
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            UserSettings(
                themeMode = ThemeMode.entries.firstOrNull { it.name == prefs[THEME_KEY] } ?: ThemeMode.SYSTEM,
                lastTrackId = prefs[LAST_TRACK_KEY],
                volume = prefs[VOLUME_KEY] ?: 1f,
            )
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME_KEY] = mode.name }
    }

    suspend fun setLastTrackId(trackId: String) {
        dataStore.edit { it[LAST_TRACK_KEY] = trackId }
    }

    suspend fun setVolume(volume: Float) {
        dataStore.edit { it[VOLUME_KEY] = volume.coerceIn(0f, 1f) }
    }

    private companion object {
        val THEME_KEY = stringPreferencesKey("theme_mode")
        val LAST_TRACK_KEY = stringPreferencesKey("last_track_id")
        val VOLUME_KEY = floatPreferencesKey("volume")
    }
}
