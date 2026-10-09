package com.ynov.spookify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ynov.spookify.domain.model.ThemeMode
import com.ynov.spookify.ui.SpookifyRoot
import com.ynov.spookify.ui.screens.player.PlayerViewModel
import com.ynov.spookify.ui.screens.settings.SettingsViewModel
import com.ynov.spookify.ui.theme.SpookifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
            val playerViewModel: PlayerViewModel = viewModel(factory = PlayerViewModel.Factory)
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()

            val darkTheme = when (settings.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            SpookifyTheme(darkTheme = darkTheme) {
                SpookifyRoot(
                    playerViewModel = playerViewModel,
                    themeMode = settings.themeMode,
                    onThemeChange = settingsViewModel::setThemeMode,
                )
            }
        }
    }
}
