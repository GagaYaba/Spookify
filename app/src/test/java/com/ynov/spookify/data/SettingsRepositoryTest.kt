package com.ynov.spookify.data

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ynov.spookify.data.repository.SettingsRepository
import com.ynov.spookify.domain.model.ThemeMode
import com.ynov.spookify.domain.model.UserSettings
import com.ynov.spookify.fakes.InMemoryPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SettingsRepositoryTest {
    private val store = InMemoryPreferencesDataStore()
    private val repository = SettingsRepository(store)

    @Test
    fun `defaults when nothing is stored`() = runTest {
        assertEquals(UserSettings(), repository.settings.first())
    }

    @Test
    fun `theme mode is persisted`() = runTest {
        repository.setThemeMode(ThemeMode.DARK)

        assertEquals(ThemeMode.DARK, repository.settings.first().themeMode)
    }

    @Test
    fun `unknown stored theme falls back to system`() = runTest {
        store.edit { it[stringPreferencesKey("theme_mode")] = "NEON" }

        assertEquals(ThemeMode.SYSTEM, repository.settings.first().themeMode)
    }

    @Test
    fun `last track id is persisted and absent by default`() = runTest {
        assertNull(repository.settings.first().lastTrackId)

        repository.setLastTrackId("abc")

        assertEquals("abc", repository.settings.first().lastTrackId)
    }

    @Test
    fun `volume is clamped between 0 and 1`() = runTest {
        repository.setVolume(3f)
        assertEquals(1f, repository.settings.first().volume)

        repository.setVolume(-1f)
        assertEquals(0f, repository.settings.first().volume)

        repository.setVolume(0.4f)
        assertEquals(0.4f, repository.settings.first().volume)
    }

    @Test
    fun `changing one setting keeps the others`() = runTest {
        repository.setThemeMode(ThemeMode.LIGHT)
        repository.setLastTrackId("abc")

        val settings = repository.settings.first()

        assertEquals(ThemeMode.LIGHT, settings.themeMode)
        assertEquals("abc", settings.lastTrackId)
    }
}
