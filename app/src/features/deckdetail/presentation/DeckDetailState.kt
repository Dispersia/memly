package io.dispersia.memly.features.deckdetail.presentation

import io.dispersia.memly.domain.card.models.Card
import io.dispersia.memly.domain.card.models.CardType
import io.dispersia.memly.domain.deck.models.Deck

data class DeckDetailState(
    val loading: Boolean = false,
    val deck: Deck? = null,
    val cards: List<Card> = emptyList(),
    val dueCount: Int = 0,
    val searchQuery: String = "",
    val showAddCardDialog: Boolean = false,
    val newCardFront: String = "",
    val newCardBack: String = "",
    val newCardType: CardType = CardType.SelfAssessed,
    val showEditCardDialog: Boolean = false,
    val editingCard: Card? = null,
    val editCardFront: String = "",
    val editCardBack: String = "",
    val editCardType: CardType = CardType.SelfAssessed,
)

sealed interface DeckDetailIntent {
    data object Load : DeckDetailIntent
    data object Refresh : DeckDetailIntent
    data object ShowAddCardDialog : DeckDetailIntent
    data object DismissAddCardDialog : DeckDetailIntent
    data class UpdateNewCardFront(val front: String) : DeckDetailIntent
    data class UpdateNewCardBack(val back: String) : DeckDetailIntent
    data class UpdateNewCardType(val type: CardType) : DeckDetailIntent
    data object ConfirmAddCard : DeckDetailIntent
    data class UpdateSearchQuery(val query: String) : DeckDetailIntent
    data class EditCard(val card: Card) : DeckDetailIntent
    data object DismissEditCardDialog : DeckDetailIntent
    data class UpdateEditCardFront(val front: String) : DeckDetailIntent
    data class UpdateEditCardBack(val back: String) : DeckDetailIntent
    data class UpdateEditCardType(val type: CardType) : DeckDetailIntent
    data object ConfirmEditCard : DeckDetailIntent
    data class DeleteCard(val cardId: Long) : DeckDetailIntent
    data object StartReview : DeckDetailIntent
}

sealed interface DeckDetailEffect {
    data class Error(val message: String) : DeckDetailEffect
    data class NavigateToReview(val deckId: Long) : DeckDetailEffect
}
