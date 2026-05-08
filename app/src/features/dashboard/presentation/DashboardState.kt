package io.dispersia.memly.features.dashboard.presentation

import io.dispersia.memly.domain.deck.models.Deck

data class DashboardState(
    val loading: Boolean = false,
    val decks: List<Deck> = emptyList<Deck>()
)

sealed interface DashboardIntent {
    data object Load : DashboardIntent
}

sealed interface DashboardEffect {
    data class Error(
        val message: String
    ) : DashboardEffect
}
