package io.dispersia.memly.domain.session

import io.dispersia.memly.domain.card.models.Card

data class SessionResult(
    val cards: List<Card>,
    val newCardCount: Int,
)

object SessionBuilder {
    fun build(
        reviewCards: List<Card>,
        newCards: List<Card>,
        cardsPerReview: Int,
        newCardBudget: Int,
    ): SessionResult {
        val shuffledNew = newCards.shuffled()
        val shuffledReview = reviewCards.shuffled()

        val newCardSlots = minOf(newCardBudget, shuffledNew.size, cardsPerReview)
        val reviewSlots = (cardsPerReview - newCardSlots).coerceAtLeast(0)

        val selectedNew = shuffledNew.take(newCardSlots)
        val selectedReview = shuffledReview.take(reviewSlots)

        val result = mutableListOf<Card>()
        if (selectedNew.isEmpty()) {
            result.addAll(selectedReview)
        } else {
            result.addAll(selectedReview)
            val interval = if (result.size > 0) (result.size + selectedNew.size) / selectedNew.size else 1
            selectedNew.forEachIndexed { i, card ->
                val insertAt = minOf((i + 1) * interval - 1, result.size)
                result.add(insertAt, card)
            }
        }

        return SessionResult(cards = result, newCardCount = selectedNew.size)
    }
}
