package io.dispersia.memly.features.review.presentation

import io.dispersia.memly.domain.card.models.Card

data class ReviewState(
    val loading: Boolean = false,
    val cards: List<Card> = emptyList(),
    val currentIndex: Int = 0,
    val isRevealed: Boolean = false,
    val typedAnswer: String = "",
    val answerResult: AnswerResult? = null,
    val isSessionComplete: Boolean = false,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
) {
    val currentCard: Card? get() = cards.getOrNull(currentIndex)
    val totalCards: Int get() = cards.size
}

enum class AnswerResult {
    Correct,
    Incorrect,
}

sealed interface ReviewIntent {
    data object Load : ReviewIntent
    data object MarkCorrect : ReviewIntent
    data object MarkIncorrect : ReviewIntent
    data class UpdateTypedAnswer(val answer: String) : ReviewIntent
    data object SubmitTypedAnswer : ReviewIntent
    data object NextCard : ReviewIntent
}

sealed interface ReviewEffect {
    data class Error(val message: String) : ReviewEffect
}
