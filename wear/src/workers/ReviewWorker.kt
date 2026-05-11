package io.dispersia.memlywear.workers

import android.content.ComponentName
import android.content.Context
import androidx.wear.tiles.TileService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.wearable.Wearable
import io.dispersia.memlywear.App
import io.dispersia.memlywear.complication.ReviewComplicationService
import io.dispersia.memlywear.tile.ReviewTileService
import kotlinx.coroutines.tasks.await
import shared.SyncPaths
import java.nio.ByteBuffer

class ReviewWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    private val appGraph by lazy { (applicationContext as App).appGraph }
    private val repo by lazy { appGraph.wearCardRepository }
    private val pendingResultStore by lazy { appGraph.wearPendingResultStore }

    override suspend fun doWork(): Result {
        flushPendingResults()

        runCatching {
            val nodes = Wearable.getNodeClient(applicationContext).connectedNodes.await()
            val messageClient = Wearable.getMessageClient(applicationContext)
            for (node in nodes) {
                messageClient.sendMessage(node.id, SyncPaths.SYNC_REQUEST, byteArrayOf()).await()
            }
        }

        val dueCount = runCatching { repo.getDueCount() }.getOrDefault(0)

        if (dueCount > 0) {
            NotificationHelper.showReviewNotification(applicationContext, dueCount)

            ComplicationDataSourceUpdateRequester
                .create(
                    applicationContext,
                    ComponentName(
                        applicationContext,
                        ReviewComplicationService::class.java
                    )
                )
                .requestUpdateAll()

            TileService
                .getUpdater(applicationContext)
                .requestUpdate(ReviewTileService::class.java)
        } else {
            NotificationHelper.dismissNotification(applicationContext)
        }

        return Result.success()
    }

    private suspend fun flushPendingResults() {
        val pending = pendingResultStore.getAll()
        if (pending.isEmpty()) return

        runCatching {
            val nodes = Wearable.getNodeClient(applicationContext).connectedNodes.await()
            val messageClient = Wearable.getMessageClient(applicationContext)
            for (result in pending) {
                val buffer = ByteBuffer.allocate(12)
                buffer.putLong(result.cardId)
                buffer.putInt(result.ratingValue)
                val data = buffer.array()
                for (node in nodes) {
                    messageClient.sendMessage(node.id, SyncPaths.REVIEW_RESULT, data).await()
                }
            }
            pendingResultStore.clear()
        }
    }
}
