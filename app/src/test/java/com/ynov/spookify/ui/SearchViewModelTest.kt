package com.ynov.spookify.ui

import androidx.lifecycle.SavedStateHandle
import com.ynov.spookify.MainDispatcherRule
import com.ynov.spookify.data.repository.TrackRepository
import com.ynov.spookify.fakes.FakeAudiusApi
import com.ynov.spookify.fakes.FakeTrackDao
import com.ynov.spookify.fakes.keepCollecting
import com.ynov.spookify.fakes.trackDto
import com.ynov.spookify.ui.screens.search.SearchUiState
import com.ynov.spookify.ui.screens.search.SearchViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api = FakeAudiusApi()

    private fun createViewModel(handle: SavedStateHandle = SavedStateHandle()) =
        SearchViewModel(TrackRepository(api, FakeTrackDao()), handle)

    // Le délai de 400 ms est virtuel : on avance l'horloge du test au-delà du debounce.
    private suspend fun TestScope.settleDebounce() {
        advanceTimeBy(DEBOUNCE_MS + 1)
        runCurrent()
    }

    @Test
    fun `blank query shows the empty state and never calls the api`() = runTest {
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        settleDebounce()

        assertEquals(SearchUiState.Vide, viewModel.state.value)
        assertEquals(emptyList(), api.searchQueries)
    }

    @Test
    fun `typing quickly triggers a single search after the debounce`() = runTest {
        api.searchResults = listOf(trackDto("a"))
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        viewModel.onQueryChange("d")
        advanceTimeBy(100)
        viewModel.onQueryChange("da")
        advanceTimeBy(100)
        viewModel.onQueryChange("daf")
        advanceTimeBy(DEBOUNCE_MS - 1)
        runCurrent()
        assertEquals(emptyList(), api.searchQueries)

        advanceTimeBy(2)
        runCurrent()

        assertEquals(listOf("daf"), api.searchQueries)
        assertEquals(listOf("a"), assertIs<SearchUiState.Succes>(viewModel.state.value).tracks.map { it.id })
    }

    @Test
    fun `query is trimmed before searching`() = runTest {
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        viewModel.onQueryChange("  daft ")
        settleDebounce()

        assertEquals(listOf("daft"), api.searchQueries)
    }

    @Test
    fun `shows loading while the search is in flight`() = runTest {
        api.gate = CompletableDeferred()
        api.searchResults = listOf(trackDto("a"))
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        viewModel.onQueryChange("daft")
        settleDebounce()
        assertEquals(SearchUiState.Chargement, viewModel.state.value)

        api.gate?.complete(Unit)

        assertIs<SearchUiState.Succes>(viewModel.state.value)
    }

    @Test
    fun `no match gives the empty state`() = runTest {
        api.searchResults = emptyList()
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        viewModel.onQueryChange("zzzz")
        settleDebounce()

        assertEquals(SearchUiState.Vide, viewModel.state.value)
    }

    @Test
    fun `network failure gives the error state and retry searches again`() = runTest {
        api.failure = IOException()
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)

        viewModel.onQueryChange("daft")
        settleDebounce()
        assertEquals(SearchUiState.Erreur("Pas de connexion internet."), viewModel.state.value)

        api.failure = null
        api.searchResults = listOf(trackDto("a"))
        viewModel.retry()

        assertEquals(listOf("daft", "daft"), api.searchQueries)
        assertIs<SearchUiState.Succes>(viewModel.state.value)
    }

    @Test
    fun `clearing the query goes back to the empty state`() = runTest {
        api.searchResults = listOf(trackDto("a"))
        val viewModel = createViewModel()
        keepCollecting(viewModel.state)
        viewModel.onQueryChange("daft")
        settleDebounce()
        assertIs<SearchUiState.Succes>(viewModel.state.value)

        viewModel.onQueryChange("")
        settleDebounce()

        assertEquals(SearchUiState.Vide, viewModel.state.value)
    }

    @Test
    fun `query lives in the saved state handle so it survives process death`() = runTest {
        api.searchResults = listOf(trackDto("a"))
        val restored = SavedStateHandle(mapOf("query" to "daft"))
        val viewModel = createViewModel(restored)
        keepCollecting(viewModel.state)

        assertEquals("daft", viewModel.query.value)
        settleDebounce()

        assertEquals(listOf("daft"), api.searchQueries)
        assertIs<SearchUiState.Succes>(viewModel.state.value)
    }

    @Test
    fun `typing updates the saved state handle`() = runTest {
        val handle = SavedStateHandle()
        val viewModel = createViewModel(handle)

        viewModel.onQueryChange("punk")

        assertEquals("punk", handle.get<String>("query"))
    }

    private companion object {
        const val DEBOUNCE_MS = 400L
    }
}
