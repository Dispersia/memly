package io.dispersia.memly.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.dispersia.memly.domain.settings.SettingsRepository
import io.dispersia.memly.domain.sync.WearDataManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val wearDataManager: WearDataManager,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<SettingsEffect>()
    val effects = _effects.asSharedFlow()

    fun dispatch(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Load -> load()
            is SettingsIntent.UpdateCardsPerReview -> updateCardsPerReview(intent.count)
            is SettingsIntent.UpdateNewCardsPerDay -> updateNewCardsPerDay(intent.count)
        }
    }

    private fun load() {
        viewModelScope.launch {
            settingsRepository.observeCardsPerReview()
                .collect { count -> _state.update { it.copy(cardsPerReview = count) } }
        }
        viewModelScope.launch {
            settingsRepository.observeNewCardsPerDay()
                .collect { count -> _state.update { it.copy(newCardsPerDay = count) } }
        }
    }

    private fun updateCardsPerReview(count: Int) {
        viewModelScope.launch {
            runCatching {
                settingsRepository.setCardsPerReview(count)
                wearDataManager.syncSettingsToWatch()
            }.onFailure {
                _effects.emit(SettingsEffect.Error("Failed to save setting"))
            }
        }
    }

    private fun updateNewCardsPerDay(count: Int) {
        viewModelScope.launch {
            runCatching {
                settingsRepository.setNewCardsPerDay(count)
                wearDataManager.syncSettingsToWatch()
            }.onFailure {
                _effects.emit(SettingsEffect.Error("Failed to save setting"))
            }
        }
    }
}
