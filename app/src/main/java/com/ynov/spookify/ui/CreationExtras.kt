package com.ynov.spookify.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.ynov.spookify.SpookifyApp

/** Point d'entrée des factories de ViewModel vers le conteneur d'injection. */
fun CreationExtras.spookifyApp(): SpookifyApp =
    requireNotNull(this[APPLICATION_KEY] as? SpookifyApp) { "L'application n'est pas SpookifyApp" }
