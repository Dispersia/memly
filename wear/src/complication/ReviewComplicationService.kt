package io.dispersia.memlywear.complication

import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import io.dispersia.memlywear.App

class ReviewComplicationService :
    SuspendingComplicationDataSourceService() {

    private val repo by lazy {
        (applicationContext as App)
            .appGraph
            .wearCardRepository
    }

    override suspend fun onComplicationRequest(
        request: ComplicationRequest
    ): ComplicationData {
        val dueCount = repo.getDueCount()

        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(
                "$dueCount due"
            ).build(),
            contentDescription = PlainComplicationText.Builder(
                "Cards due"
            ).build()
        )
            .setTapAction(null)
            .build()
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(
                "0 due"
            ).build(),
            contentDescription = PlainComplicationText.Builder(
                "Cards Due"
            ).build()
        ).build()
    }
}
