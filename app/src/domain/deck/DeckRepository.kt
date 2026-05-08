package io.dispersia.memly.domain.deck

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.dispersia.memly.domain.deck.models.Deck

interface DeckRepository {
    suspend fun loadDecks(): List<Deck>
}

@ContributesBinding(AppScope::class)
@Inject
class DeckRepositoryImpl : DeckRepository {
    override suspend fun loadDecks(): List<Deck> {
        return listOf(Deck(name = "Deck 1"), Deck(name = "Deck 2"))
    }
}

