@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package io.dispersia.memly.features.deckdetail.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.dispersia.memly.domain.card.CardRepository
import io.dispersia.memly.domain.card.models.CardType
import io.dispersia.memly.domain.deck.DeckRepository
import io.dispersia.memly.domain.sync.WearDataManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

class DeckDetailViewModel(
    private val deckId: Long,
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val wearDataManager: WearDataManager,
) : ViewModel() {
    private val _state = MutableStateFlow(DeckDetailState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<DeckDetailEffect>()
    val effects = _effects.asSharedFlow()

    private val refreshTrigger = MutableStateFlow(0L)

    fun dispatch(intent: DeckDetailIntent) {
        when (intent) {
            DeckDetailIntent.Load -> load()
            DeckDetailIntent.Refresh -> refreshTrigger.value++
            DeckDetailIntent.ShowAddCardDialog ->
                _state.update {
                    it.copy(
                        showAddCardDialog = true,
                        newCardFront = "",
                        newCardBack = "",
                        newCardType = CardType.SelfAssessed,
                    )
                }
            DeckDetailIntent.DismissAddCardDialog ->
                _state.update { it.copy(showAddCardDialog = false) }
            is DeckDetailIntent.UpdateNewCardFront ->
                _state.update { it.copy(newCardFront = intent.front) }
            is DeckDetailIntent.UpdateNewCardBack ->
                _state.update { it.copy(newCardBack = intent.back) }
            is DeckDetailIntent.UpdateNewCardType ->
                _state.update { it.copy(newCardType = intent.type) }
            DeckDetailIntent.ConfirmAddCard -> addCard()
            is DeckDetailIntent.UpdateSearchQuery ->
                _state.update { it.copy(searchQuery = intent.query) }
            is DeckDetailIntent.EditCard ->
                _state.update {
                    it.copy(
                        showEditCardDialog = true,
                        editingCard = intent.card,
                        editCardFront = intent.card.front,
                        editCardBack = intent.card.back,
                        editCardType = intent.card.type,
                    )
                }
            DeckDetailIntent.DismissEditCardDialog ->
                _state.update { it.copy(showEditCardDialog = false, editingCard = null) }
            is DeckDetailIntent.UpdateEditCardFront ->
                _state.update { it.copy(editCardFront = intent.front) }
            is DeckDetailIntent.UpdateEditCardBack ->
                _state.update { it.copy(editCardBack = intent.back) }
            is DeckDetailIntent.UpdateEditCardType ->
                _state.update { it.copy(editCardType = intent.type) }
            DeckDetailIntent.ConfirmEditCard -> editCard()
            is DeckDetailIntent.DeleteCard -> deleteCard(intent.cardId)
            DeckDetailIntent.StartReview -> {
                viewModelScope.launch { _effects.emit(DeckDetailEffect.NavigateToReview(deckId)) }
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            val deck = deckRepository.getDeckById(deckId)
            _state.update { it.copy(deck = deck) }
        }
        viewModelScope.launch {
            cardRepository.observeCardsByDeckId(deckId)
                .catch { _effects.emit(DeckDetailEffect.Error("Failed to load cards")) }
                .collect { cards -> _state.update { it.copy(loading = false, cards = cards) } }
        }
        viewModelScope.launch {
            refreshTrigger.flatMapLatest {
                cardRepository.observeDueCount(deckId, Instant.now())
            }.collect { count -> _state.update { it.copy(dueCount = count) } }
        }
    }

    private fun addCard() {
        val front = _state.value.newCardFront.trim()
        val back = _state.value.newCardBack.trim()
        val type = _state.value.newCardType
        if (front.isBlank() || back.isBlank()) return

        viewModelScope.launch {
            runCatching { cardRepository.createCard(deckId, front, back, type) }
                .onSuccess {
                    _state.update { it.copy(showAddCardDialog = false) }
                    runCatching {
                        Log.d("DeckDetail", "Syncing to watch after add...")
                        wearDataManager.syncDueCardsToWatch()
                        Log.d("DeckDetail", "Watch sync complete after add")
                    }.onFailure { e ->
                        Log.e("DeckDetail", "Watch sync failed after add", e)
                    }
                }
                .onFailure { _effects.emit(DeckDetailEffect.Error("Failed to add card")) }
        }
    }

    private fun editCard() {
        val card = _state.value.editingCard ?: return
        val front = _state.value.editCardFront.trim()
        val back = _state.value.editCardBack.trim()
        val type = _state.value.editCardType
        if (front.isBlank() || back.isBlank()) return

        val updatedCard = card.copy(front = front, back = back, type = type)
        viewModelScope.launch {
            runCatching { cardRepository.updateCard(updatedCard) }
                .onSuccess {
                    _state.update { it.copy(showEditCardDialog = false, editingCard = null) }
                    runCatching { wearDataManager.syncDueCardsToWatch() }
                }
                .onFailure { _effects.emit(DeckDetailEffect.Error("Failed to update card")) }
        }
    }

    private fun deleteCard(cardId: Long) {
        viewModelScope.launch {
            runCatching { cardRepository.deleteCard(cardId) }
                .onSuccess { runCatching { wearDataManager.syncDueCardsToWatch() } }
                .onFailure { _effects.emit(DeckDetailEffect.Error("Failed to delete card")) }
        }
    }
}
