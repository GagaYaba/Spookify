package com.ynov.spookify.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ynov.spookify.data.repository.SettingsRepository
import com.ynov.spookify.domain.model.ThemeMode
import com.ynov.spookify.domain.model.UserSettings
import com.ynov.spookify.ui.spookifyApp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Porté par l'activité : le thème doit être connu avant d'afficher le moindre écran. */
class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val settings: StateFlow<UserSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserSettings())

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { SettingsViewModel(spookifyApp().container.settingsRepository) }
        }
    }
}
