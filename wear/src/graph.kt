package io.dispersia.memlywear

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders

@DependencyGraph(AppScope::class)
interface AppGraph : MetroAppComponentProviders {
    val counterRepository: CounterRepository
}
