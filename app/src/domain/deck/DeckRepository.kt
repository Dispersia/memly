package io.dispersia.memly.domain.deck

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.dispersia.memly.domain.deck.data.DeckDao
import io.dispersia.memly.domain.deck.data.toDomain
import io.dispersia.memly.domain.deck.data.toEntity
import io.dispersia.memly.domain.deck.models.Deck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface DeckRepository {
    fun observeDecks(): Flow<List<Deck>>
    suspend fun getDeckById(id: Long): Deck?
    suspend fun createDeck(name: String): Long
    suspend fun deleteDeck(id: Long)
}

@ContributesBinding(AppScope::class)
@Inject
class DeckRepositoryImpl(
    private val deckDao: DeckDao,
) : DeckRepository {
    override fun observeDecks(): Flow<List<Deck>> =
        deckDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getDeckById(id: Long): Deck? =
        deckDao.getById(id)?.toDomain()

    override suspend fun createDeck(name: String): Long =
        deckDao.insert(Deck(name = name).toEntity())

    override suspend fun deleteDeck(id: Long) =
        deckDao.deleteById(id)
}
