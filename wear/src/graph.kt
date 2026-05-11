package io.dispersia.memlywear

import android.app.Application
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import io.dispersia.memlywear.offline.WearPendingResultStore

@DependencyGraph(AppScope::class)
interface AppGraph : MetroAppComponentProviders {
    val wearCardRepository: WearCardRepository
    val wearPendingResultStore: WearPendingResultStore

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AppGraph
    }
}
