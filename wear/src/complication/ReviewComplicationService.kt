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

    val graph =
        (applicationContext as App)
            .appGraph
            .counterRepository

    override suspend fun onComplicationRequest(
        request: ComplicationRequest
    ): ComplicationData? {
        val dueCount = graph.loadInitialCount()

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
        val dueCount = 0

        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(
                "$dueCount due"
            ).build(),
            contentDescription = PlainComplicationText.Builder(
            "Cards Due"
        ).build()
        ).build()
    }
}