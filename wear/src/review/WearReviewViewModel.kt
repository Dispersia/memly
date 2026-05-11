package io.dispersia.memlywear.review

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.Wearable
import io.dispersia.memlywear.WearCardRepository
import io.dispersia.memlywear.offline.PendingResult
import io.dispersia.memlywear.offline.WearPendingResultStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import shared.SyncPaths
import java.nio.ByteBuffer

class WearReviewViewModel(
    private val application: Application,
    private val repository: WearCardRepository,
    private val pendingResultStore: WearPendingResultStore,
) : ViewModel() {
    private val _state = MutableStateFlow(WearReviewState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<WearReviewEffect>()
    val effects = _effects.asSharedFlow()

    fun dispatch(intent: WearReviewIntent) {
        when (intent) {
            WearReviewIntent.Load -> load()
            WearReviewIntent.MarkCorrect -> recordAnswer(RATING_GOOD, AnswerResult.Correct)
            WearReviewIntent.MarkIncorrect -> recordAnswer(RATING_AGAIN, AnswerResult.Incorrect)
            WearReviewIntent.NextCard -> nextCard()
            is WearReviewIntent.UpdateTypedAnswer ->
                _state.update { it.copy(typedAnswer = intent.answer) }
            WearReviewIntent.SubmitTypedAnswer -> submitTypedAnswer()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            runCatching {
                val answeredIds = pendingResultStore.getAnsweredCardIds()
                repository.getDueCards().filter { it.id !in answeredIds }
            }.onSuccess { cards ->
                _state.update {
                    it.copy(
                        loading = false,
                        cards = cards,
                        currentIndex = 0,
                        isRevealed = false,
                        isSessionComplete = cards.isEmpty(),
                    )
                }
            }.onFailure {
                _state.update { it.copy(loading = false) }
                _effects.emit(WearReviewEffect.Error("Failed to load cards"))
            }
        }
    }

    private fun submitTypedAnswer() {
        val card = _state.value.currentCard ?: return
        val typed = _state.value.typedAnswer.trim()
        val isCorrect = typed.equals(card.back.trim(), ignoreCase = true)
        val result = if (isCorrect) AnswerResult.Correct else AnswerResult.Incorrect
        val ratingValue = if (isCorrect) RATING_GOOD else RATING_AGAIN
        recordAnswer(ratingValue, result)
    }

    private fun recordAnswer(ratingValue: Int, result: AnswerResult) {
        val card = _state.value.currentCard ?: return

        pendingResultStore.save(
            PendingResult(
                cardId = card.id,
                ratingValue = ratingValue,
                timestampMillis = System.currentTimeMillis(),
            )
        )

        viewModelScope.launch {
            runCatching {
                val buffer = ByteBuffer.allocate(12)
                buffer.putLong(card.id)
                buffer.putInt(ratingValue)
                val data = buffer.array()

                val nodes = Wearable.getNodeClient(application).connectedNodes.await()
                val messageClient = Wearable.getMessageClient(application)
                for (node in nodes) {
                    messageClient.sendMessage(node.id, SyncPaths.REVIEW_RESULT, data).await()
                }
            }
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

    companion object {
        private const val RATING_AGAIN = 1
        private const val RATING_GOOD = 3
    }
}
