package io.dispersia.memlywear.tile

import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.material3.*
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.types.LayoutString
import androidx.wear.tiles.*
import io.dispersia.memlywear.App

class ReviewTileService : Material3TileService() {

    private val repo by lazy {
        (application as App)
            .appGraph
            .counterRepository
    }

    override suspend fun MaterialScope.tileResponse(
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {
        val count = repo.loadInitialCount()

        val layout = primaryLayout(
            titleSlot = null,
            mainSlot = {
                text(
                    text = LayoutString("$count cards due"),
                    typography = Typography.TITLE_MEDIUM
                )
            },
            onClick = clickable(
                id = "open_review",
                action = ActionBuilders.LaunchAction.Builder()
                    .setAndroidActivity(
                        ActionBuilders.AndroidActivity.Builder()
                            .setPackageName("io.dispersia.memlywear")
                            .setClassName("io.dispersia.memlywear.presentation.MainActivity")
                            .build()
                    )
                    .build()
            )
        )

        return tile(
            timeline = timeline(
                timelineEntry(
                    layout = layout
                )
            )
        )
    }
}
