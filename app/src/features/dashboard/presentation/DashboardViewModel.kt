package io.dispersia.memly.features.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.dispersia.memly.domain.deck.DeckRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val deckRepository: DeckRepository,
    private val cardRepository: io.dispersia.memly.domain.card.CardRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<DashboardEffect>()
    val effects = _effects.asSharedFlow()

    fun dispatch(intent: DashboardIntent) {
        when (intent) {
            DashboardIntent.Load -> load()
            DashboardIntent.ShowCreateDeckDialog ->
                _state.update { it.copy(showCreateDeckDialog = true, newDeckName = "") }
            DashboardIntent.DismissCreateDeckDialog ->
                _state.update { it.copy(showCreateDeckDialog = false, newDeckName = "") }
            is DashboardIntent.UpdateNewDeckName ->
                _state.update { it.copy(newDeckName = intent.name) }
            DashboardIntent.ConfirmCreateDeck -> createDeck()
            is DashboardIntent.DeckClicked -> {
                viewModelScope.launch { _effects.emit(DashboardEffect.NavigateToDeck(intent.deckId)) }
            }
            DashboardIntent.OpenSettings -> {
                viewModelScope.launch { _effects.emit(DashboardEffect.NavigateToSettings) }
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            deckRepository.observeDecks()
                .catch {
                    _state.update { it.copy(loading = false) }
                    _effects.emit(DashboardEffect.Error("Failed to load decks"))
                }
                .collect { decks ->
                    _state.update { it.copy(loading = false, decks = decks) }
                }
        }
        viewModelScope.launch {
            cardRepository.observeDueCountsByDeck()
                .collect { counts ->
                    _state.update { it.copy(dueCounts = counts) }
                }
        }
    }

    private fun createDeck() {
        val name = _state.value.newDeckName.trim()
        if (name.isBlank()) return

        viewModelScope.launch {
            runCatching { deckRepository.createDeck(name) }
                .onSuccess {
                    _state.update { it.copy(showCreateDeckDialog = false, newDeckName = "") }
                }
                .onFailure {
                    _effects.emit(DashboardEffect.Error("Failed to create deck"))
                }
        }
    }
}
