package io.dispersia.memlywear.sync

import android.content.ComponentName
import androidx.wear.tiles.TileService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService
import io.dispersia.memlywear.complication.ReviewComplicationService
import io.dispersia.memlywear.tile.ReviewTileService
import shared.SyncPaths

class WearDataListenerService : WearableListenerService() {
    override fun onDataChanged(events: DataEventBuffer) {
        var shouldUpdate = false
        for (event in events) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path ?: continue
                if (path == SyncPaths.DUE_COUNT || path == SyncPaths.DUE_CARDS) {
                    shouldUpdate = true
                    break
                }
            }
        }

        if (shouldUpdate) {
            TileService.getUpdater(this).requestUpdate(ReviewTileService::class.java)

            ComplicationDataSourceUpdateRequester
                .create(this, ComponentName(this, ReviewComplicationService::class.java))
                .requestUpdateAll()
        }
    }
}
