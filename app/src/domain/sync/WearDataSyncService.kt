package io.dispersia.memly.domain.sync

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import io.dispersia.memly.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import shared.SyncKeys
import shared.SyncPaths
import java.nio.ByteBuffer

class WearDataSyncService : WearableListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val wearDataManager: WearDataManager by lazy {
        (application as App).appGraph.wearDataManager
    }

    override fun onMessageReceived(event: MessageEvent) {
        when (event.path) {
            SyncPaths.SYNC_REQUEST -> {
                scope.launch { wearDataManager.syncDueCardsToWatch() }
            }
            SyncPaths.REVIEW_RESULT -> {
                val buffer = ByteBuffer.wrap(event.data)
                val cardId = buffer.getLong()
                val rating = buffer.getInt()
                scope.launch { wearDataManager.handleReviewResult(cardId, rating) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
