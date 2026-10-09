package com.ynov.spookify.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ynov.spookify.data.repository.TrackRepository
import com.ynov.spookify.ui.spookifyApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class SearchViewModel(
    private val trackRepository: TrackRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Dans le SavedStateHandle : le texte saisi survit à la rotation et à la mort du process.
    val query: StateFlow<String> = savedStateHandle.getStateFlow(QUERY_KEY, "")

    private val retryTick = MutableStateFlow(0)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val state: StateFlow<SearchUiState> = combine(
        query.debounce(DEBOUNCE_MS).map { it.trim() }.distinctUntilChanged(),
        retryTick,
    ) { text, _ -> text }
        .flatMapLatest(::searchFlow)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), SearchUiState.Vide)

    fun onQueryChange(value: String) {
        savedStateHandle[QUERY_KEY] = value
    }

    fun retry() {
        retryTick.update { it + 1 }
    }

    private fun searchFlow(text: String): Flow<SearchUiState> =
        if (text.isBlank()) {
            flowOf(SearchUiState.Vide)
        } else {
            flow {
                emit(SearchUiState.Chargement)
                val failure = trackRepository.refreshSearch(text).exceptionOrNull()
                emitAll(
                    trackRepository.observeSearch(text).map { tracks ->
                        when {
                            tracks.isNotEmpty() -> SearchUiState.Succes(tracks)
                            failure != null -> SearchUiState.Erreur(failure.message ?: DEFAULT_ERROR)
                            else -> SearchUiState.Vide
                        }
                    }
                )
            }
        }

    companion object {
        private const val QUERY_KEY = "query"
        private const val DEBOUNCE_MS = 400L
        private const val STOP_TIMEOUT_MS = 5_000L
        private const val DEFAULT_ERROR = "Une erreur est survenue."

        val Factory = viewModelFactory {
            initializer { SearchViewModel(spookifyApp().container.trackRepository, createSavedStateHandle()) }
        }
    }
}
