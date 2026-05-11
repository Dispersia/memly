package io.dispersia.memlywear

import android.app.Application
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.Wearable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.dispersia.memlywear.models.WearCard
import io.dispersia.memlywear.models.WearCardType
import kotlinx.coroutines.tasks.await
import shared.SyncDefaults
import shared.SyncKeys
import shared.SyncPaths

interface WearCardRepository {
    suspend fun getDueCards(): List<WearCard>
    suspend fun getDueCount(): Int
    suspend fun getCardsPerReview(): Int
    suspend fun getNewCardsPerDay(): Int
}

@ContributesBinding(AppScope::class)
@Inject
class WearCardRepositoryImpl(
    private val application: Application,
) : WearCardRepository {
    private val dataClient: DataClient by lazy { Wearable.getDataClient(application) }

    override suspend fun getDueCards(): List<WearCard> {
        val dataItems = dataClient.getDataItems(
            android.net.Uri.parse("wear://*${SyncPaths.DUE_CARDS}")
        ).await()

        val cards = mutableListOf<WearCard>()
        for (item in dataItems) {
            val dataMap = com.google.android.gms.wearable.DataMapItem.fromDataItem(item).dataMap
            val cardMaps = dataMap.getDataMapArrayList(SyncKeys.CARDS) ?: continue
            for (map in cardMaps) {
                cards.add(
                    WearCard(
                        id = map.getLong(SyncKeys.ID),
                        deckId = map.getLong(SyncKeys.DECK_ID),
                        front = map.getString(SyncKeys.FRONT)!!,
                        back = map.getString(SyncKeys.BACK)!!,
                        type = try {
                            WearCardType.valueOf(map.getString(SyncKeys.TYPE)!!)
                        } catch (_: Exception) {
                            WearCardType.SelfAssessed
                        },
                    )
                )
            }
        }
        dataItems.release()
        return cards
    }

    override suspend fun getDueCount(): Int {
        val dataItems = dataClient.getDataItems(
            android.net.Uri.parse("wear://*${SyncPaths.DUE_COUNT}")
        ).await()

        var count = 0
        for (item in dataItems) {
            val dataMap = com.google.android.gms.wearable.DataMapItem.fromDataItem(item).dataMap
            count = dataMap.getInt(SyncKeys.COUNT)
        }
        dataItems.release()
        return count
    }

    override suspend fun getCardsPerReview(): Int {
        val dataItems = dataClient.getDataItems(
            android.net.Uri.parse("wear://*${SyncPaths.SETTINGS}")
        ).await()

        var cardsPerReview = SyncDefaults.DEFAULT_CARDS_PER_REVIEW
        for (item in dataItems) {
            val dataMap = com.google.android.gms.wearable.DataMapItem.fromDataItem(item).dataMap
            cardsPerReview = dataMap.getInt(SyncKeys.CARDS_PER_REVIEW, SyncDefaults.DEFAULT_CARDS_PER_REVIEW)
        }
        dataItems.release()
        return cardsPerReview
    }

    override suspend fun getNewCardsPerDay(): Int {
        val dataItems = dataClient.getDataItems(
            android.net.Uri.parse("wear://*${SyncPaths.SETTINGS}")
        ).await()

        var newCardsPerDay = SyncDefaults.DEFAULT_NEW_CARDS_PER_DAY
        for (item in dataItems) {
            val dataMap = com.google.android.gms.wearable.DataMapItem.fromDataItem(item).dataMap
            newCardsPerDay = dataMap.getInt(SyncKeys.NEW_CARDS_PER_DAY, SyncDefaults.DEFAULT_NEW_CARDS_PER_DAY)
        }
        dataItems.release()
        return newCardsPerDay
    }
}
