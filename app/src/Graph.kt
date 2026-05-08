package io.dispersia.memly

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import io.dispersia.memly.domain.deck.DeckRepository

@DependencyGraph(AppScope::class)
interface AppGraph : MetroAppComponentProviders {
    val deckRepository: DeckRepository
}
