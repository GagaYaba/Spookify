package com.ynov.spookify.ui.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ynov.spookify.R
import com.ynov.spookify.ui.components.EmptyView
import com.ynov.spookify.ui.components.ErrorView
import com.ynov.spookify.ui.components.LoadingView
import com.ynov.spookify.ui.components.ScreenHeader
import com.ynov.spookify.ui.components.TrackActions
import com.ynov.spookify.ui.components.TrackRow

@Composable
fun SearchScreen(
    query: String,
    state: SearchUiState,
    actions: TrackActions,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        ScreenHeader(title = stringResource(R.string.search_title), onBack = onBack)
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.search_clear))
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
        )
        when (state) {
            SearchUiState.Chargement -> LoadingView()
            SearchUiState.Vide -> EmptyView(
                if (query.isBlank()) stringResource(R.string.search_prompt) else stringResource(R.string.search_no_result, query.trim())
            )
            is SearchUiState.Erreur -> ErrorView(state.message, onRetry = onRetry)
            is SearchUiState.Succes -> LazyColumn(Modifier.fillMaxSize()) {
                items(state.tracks, key = { it.id }) { track ->
                    TrackRow(
                        track = track,
                        isCurrent = track.id == actions.currentTrackId,
                        onClick = { actions.onPlay(state.tracks, track.id) },
                        onToggleFavorite = { actions.onToggleFavorite(track) },
                    )
                }
            }
        }
    }
}
