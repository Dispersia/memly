package io.dispersia.memly.features.settings.presentation

data class SettingsState(
    val cardsPerReview: Int = 20,
    val newCardsPerDay: Int = 10,
)

sealed interface SettingsIntent {
    data object Load : SettingsIntent
    data class UpdateCardsPerReview(val count: Int) : SettingsIntent
    data class UpdateNewCardsPerDay(val count: Int) : SettingsIntent
}

sealed interface SettingsEffect {
    data class Error(val message: String) : SettingsEffect
}
