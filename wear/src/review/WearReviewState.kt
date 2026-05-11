package io.dispersia.memlywear.review

import io.dispersia.memlywear.models.WearCard

data class WearReviewState(
    val loading: Boolean = false,
    val cards: List<WearCard> = emptyList(),
    val currentIndex: Int = 0,
    val isRevealed: Boolean = false,
    val isSessionComplete: Boolean = false,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val typedAnswer: String = "",
    val answerResult: AnswerResult? = null,
) {
    val currentCard: WearCard? get() = cards.getOrNull(currentIndex)
    val totalCards: Int get() = cards.size
}

enum class AnswerResult {
    Correct,
    Incorrect,
}

sealed interface WearReviewIntent {
    data object Load : WearReviewIntent
    data object MarkCorrect : WearReviewIntent
    data object MarkIncorrect : WearReviewIntent
    data object NextCard : WearReviewIntent
    data class UpdateTypedAnswer(val answer: String) : WearReviewIntent
    data object SubmitTypedAnswer : WearReviewIntent
}

sealed interface WearReviewEffect {
    data class Error(val message: String) : WearReviewEffect
}
