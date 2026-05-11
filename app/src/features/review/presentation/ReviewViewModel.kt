package io.dispersia.memly.features.review.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.dispersia.memly.domain.card.CardRepository
import io.dispersia.memly.domain.scheduler.FsrsRating
import io.dispersia.memly.domain.scheduler.FsrsScheduler
import io.dispersia.memly.domain.session.SessionBuilder
import io.dispersia.memly.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

class ReviewViewModel(
    private val deckId: Long,
    private val cardRepository: CardRepository,
    private val settingsRepository: SettingsRepository,
    private val scheduler: FsrsScheduler = FsrsScheduler(),
) : ViewModel() {
    private val _state = MutableStateFlow(ReviewState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<ReviewEffect>()
    val effects = _effects.asSharedFlow()

    fun dispatch(intent: ReviewIntent) {
        when (intent) {
            ReviewIntent.Load -> load()
            ReviewIntent.MarkCorrect -> recordAnswer(FsrsRating.Good, AnswerResult.Correct)
            ReviewIntent.MarkIncorrect -> recordAnswer(FsrsRating.Again, AnswerResult.Incorrect)
            is ReviewIntent.UpdateTypedAnswer ->
                _state.update { it.copy(typedAnswer = intent.answer) }
            ReviewIntent.SubmitTypedAnswer -> submitTypedAnswer()
            ReviewIntent.NextCard -> nextCard()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            runCatching {
                val cardsPerReview = settingsRepository.getCardsPerReview()
                val newCardsPerDay = settingsRepository.getNewCardsPerDay()
                val newCardsShownToday = settingsRepository.getNewCardsShownToday()
                val newCardBudget = (newCardsPerDay - newCardsShownToday).coerceAtLeast(0)

                val reviewCards = cardRepository.getDueReviewCardsByDeckId(deckId)
                val newCards = cardRepository.getNewCardsByDeckId(deckId)

                val session = SessionBuilder.build(reviewCards, newCards, cardsPerReview, newCardBudget)
                settingsRepository.incrementNewCardsShownToday(session.newCardCount)
                session.cards
            }.onSuccess { cards ->
                _state.update {
                    it.copy(
                        loading = false,
                        cards = cards,
                        currentIndex = 0,
                        isRevealed = false,
                        typedAnswer = "",
                        answerResult = null,
                        isSessionComplete = cards.isEmpty(),
                    )
                }
            }.onFailure {
                _state.update { it.copy(loading = false) }
                _effects.emit(ReviewEffect.Error("Failed to load cards"))
            }
        }
    }

    private fun submitTypedAnswer() {
        val currentCard = _state.value.currentCard ?: return
        val typed = _state.value.typedAnswer.trim()
        val isCorrect = typed == currentCard.back.trim()
        val rating = if (isCorrect) FsrsRating.Good else FsrsRating.Again
        recordAnswer(rating, if (isCorrect) AnswerResult.Correct else AnswerResult.Incorrect)
    }

    private fun recordAnswer(rating: FsrsRating, result: AnswerResult) {
        val currentCard = _state.value.currentCard ?: return
        val now = Instant.now()
        val scheduling = scheduler.schedule(currentCard, rating, now)

        val updatedCard = currentCard.copy(
            stability = scheduling.stability,
            difficulty = scheduling.difficulty,
            interval = scheduling.interval,
            dueDate = scheduling.dueDate,
            lastReview = now,
            reps = scheduling.reps,
            lapses = scheduling.lapses,
        )

        viewModelScope.launch {
            runCatching { cardRepository.updateCard(updatedCard) }
                .onFailure { _effects.emit(ReviewEffect.Error("Failed to save progress")) }
        }

        _state.update {
            it.copy(
                isRevealed = true,
                answerResult = result,
                correctCount = it.correctCount + if (result == AnswerResult.Correct) 1 else 0,
                incorrectCount = it.incorrectCount + if (result == AnswerResult.Incorrect) 1 else 0,
            )
        }
    }

    private fun nextCard() {
        _state.update {
            val nextIndex = it.currentIndex + 1
            if (nextIndex >= it.cards.size) {
                it.copy(isSessionComplete = true)
            } else {
                it.copy(
                    currentIndex = nextIndex,
                    isRevealed = false,
                    typedAnswer = "",
                    answerResult = null,
                )
            }
        }
    }
}
