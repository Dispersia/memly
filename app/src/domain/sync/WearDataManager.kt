package io.dispersia.memly.domain.sync

import android.app.Application
import android.util.Log
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.dispersia.memly.domain.card.CardRepository
import io.dispersia.memly.domain.scheduler.FsrsRating
import io.dispersia.memly.domain.scheduler.FsrsScheduler
import io.dispersia.memly.domain.settings.SettingsRepository
import kotlinx.coroutines.tasks.await
import shared.SyncKeys
import shared.SyncPaths
import java.time.Instant

interface WearDataManager {
    suspend fun syncDueCardsToWatch()
    suspend fun syncSettingsToWatch()
    suspend fun handleReviewResult(cardId: Long, ratingValue: Int)
}

@ContributesBinding(AppScope::class)
@Inject
class WearDataManagerImpl(
    private val application: Application,
    private val cardRepository: CardRepository,
    private val settingsRepository: SettingsRepository,
    private val scheduler: FsrsScheduler = FsrsScheduler(),
) : WearDataManager {
    private val dataClient by lazy { Wearable.getDataClient(application) }

    override suspend fun syncDueCardsToWatch() {
        val dueCards = cardRepository.getAllDueCards()
        Log.d(TAG, "syncDueCardsToWatch: ${dueCards.size} due cards")

        val cardMaps = dueCards.map { card ->
            DataMap().apply {
                putLong(SyncKeys.ID, card.id)
                putLong(SyncKeys.DECK_ID, card.deckId)
                putString(SyncKeys.FRONT, card.front)
                putString(SyncKeys.BACK, card.back)
                putString(SyncKeys.TYPE, card.type.name)
            }
        }

        val request = PutDataMapRequest.create(SyncPaths.DUE_CARDS).apply {
            dataMap.putDataMapArrayList(SyncKeys.CARDS, ArrayList(cardMaps))
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }
        dataClient.putDataItem(request.asPutDataRequest().setUrgent()).await()
        Log.d(TAG, "syncDueCardsToWatch: cards DataItem put successfully")

        val countRequest = PutDataMapRequest.create(SyncPaths.DUE_COUNT).apply {
            dataMap.putInt(SyncKeys.COUNT, dueCards.size)
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }
        dataClient.putDataItem(countRequest.asPutDataRequest().setUrgent()).await()
        Log.d(TAG, "syncDueCardsToWatch: count DataItem put successfully")
    }

    override suspend fun syncSettingsToWatch() {
        val cardsPerReview = settingsRepository.getCardsPerReview()
        val newCardsPerDay = settingsRepository.getNewCardsPerDay()
        val request = PutDataMapRequest.create(SyncPaths.SETTINGS).apply {
            dataMap.putInt(SyncKeys.CARDS_PER_REVIEW, cardsPerReview)
            dataMap.putInt(SyncKeys.NEW_CARDS_PER_DAY, newCardsPerDay)
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }
        dataClient.putDataItem(request.asPutDataRequest().setUrgent()).await()
    }

    override suspend fun handleReviewResult(cardId: Long, ratingValue: Int) {
        val card = cardRepository.getCardById(cardId) ?: return
        val rating = FsrsRating.entries.firstOrNull { it.value == ratingValue } ?: return
        val now = Instant.now()
        val result = scheduler.schedule(card, rating, now)

        val updatedCard = card.copy(
            stability = result.stability,
            difficulty = result.difficulty,
            interval = result.interval,
            dueDate = result.dueDate,
            lastReview = now,
            reps = result.reps,
            lapses = result.lapses,
        )
        cardRepository.updateCard(updatedCard)
        syncDueCardsToWatch()
    }

    companion object {
        private const val TAG = "WearDataManager"
    }
}
