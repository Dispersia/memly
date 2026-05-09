package io.dispersia.memlywear.workers

import android.content.ComponentName
import android.content.Context
import androidx.wear.tiles.TileService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.dispersia.memlywear.App
import io.dispersia.memlywear.complication.ReviewComplicationService
import io.dispersia.memlywear.tile.ReviewTileService

class ReviewWorker(
    context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params) {
    private val repo =
        (applicationContext as App)
            .appGraph
            .counterRepository

    override suspend fun doWork(): Result {
        val hasDueCards = repo.loadInitialCount() > 0

        if (hasDueCards) {
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
        }

        return Result.success()
    }
}