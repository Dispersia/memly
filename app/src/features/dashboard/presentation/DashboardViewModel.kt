package io.dispersia.memly.features.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.dispersia.memly.domain.deck.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val deckRepository: DeckRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    fun dispatch(intent: DashboardIntent) {
        when (intent) {
            DashboardIntent.Load -> {
                load()
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update {
                it.copy(loading = true)
            }

            runCatching {
                deckRepository.loadDecks()
            }.onSuccess { decks ->
                _state.update {
                    it.copy(
                        loading = false,
                        decks = decks
                    )
                }
            }.onFailure {
                _state.update {
                    it.copy(loading = false)
                }
            }
        }
    }
}
