package io.dispersia.memly.features.dashboard.presentation

import io.dispersia.memly.domain.deck.models.Deck

data class DashboardState(
    val loading: Boolean = false,
    val decks: List<Deck> = emptyList(),
    val dueCounts: Map<Long, Int> = emptyMap(),
    val showCreateDeckDialog: Boolean = false,
    val newDeckName: String = "",
)

sealed interface DashboardIntent {
    data object Load : DashboardIntent
    data object ShowCreateDeckDialog : DashboardIntent
    data object DismissCreateDeckDialog : DashboardIntent
    data class UpdateNewDeckName(val name: String) : DashboardIntent
    data object ConfirmCreateDeck : DashboardIntent
    data class DeckClicked(val deckId: Long) : DashboardIntent
    data object OpenSettings : DashboardIntent
}

sealed interface DashboardEffect {
    data class Error(val message: String) : DashboardEffect
    data class NavigateToDeck(val deckId: Long) : DashboardEffect
    data object NavigateToSettings : DashboardEffect
}
