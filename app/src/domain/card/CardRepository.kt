package io.dispersia.memly.domain.card

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.dispersia.memly.domain.card.data.CardDao
import io.dispersia.memly.domain.card.data.DeckDueCount
import io.dispersia.memly.domain.card.data.toDomain
import io.dispersia.memly.domain.card.data.toEntity
import io.dispersia.memly.domain.card.models.Card
import io.dispersia.memly.domain.card.models.CardType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

interface CardRepository {
    fun observeCardsByDeckId(deckId: Long): Flow<List<Card>>
    suspend fun getDueCardsByDeckId(deckId: Long, now: Instant = Instant.now()): List<Card>
    fun observeDueCount(deckId: Long, now: Instant = Instant.now()): Flow<Int>
    suspend fun createCard(deckId: Long, front: String, back: String, type: CardType): Long
    suspend fun updateCard(card: Card)
    suspend fun deleteCard(id: Long)
    suspend fun getCardById(id: Long): Card?
    suspend fun getAllDueCards(now: Instant = Instant.now()): List<Card>
    suspend fun getTotalDueCount(now: Instant = Instant.now()): Int
    fun observeDueCountsByDeck(now: Instant = Instant.now()): Flow<Map<Long, Int>>
    suspend fun getNewCardsByDeckId(deckId: Long): List<Card>
    suspend fun getAllNewCards(): List<Card>
    suspend fun getDueReviewCardsByDeckId(deckId: Long, now: Instant = Instant.now()): List<Card>
    suspend fun getAllDueReviewCards(now: Instant = Instant.now()): List<Card>
}

@ContributesBinding(AppScope::class)
@Inject
class CardRepositoryImpl(
    private val cardDao: CardDao,
) : CardRepository {
    override fun observeCardsByDeckId(deckId: Long): Flow<List<Card>> =
        cardDao.observeByDeckId(deckId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getDueCardsByDeckId(deckId: Long, now: Instant): List<Card> =
        cardDao.getDueByDeckId(deckId, now.toEpochMilli()).map { it.toDomain() }

    override fun observeDueCount(deckId: Long, now: Instant): Flow<Int> =
        cardDao.observeDueCount(deckId, now.toEpochMilli())

    override suspend fun createCard(deckId: Long, front: String, back: String, type: CardType): Long =
        cardDao.insert(
            Card(deckId = deckId, front = front, back = back, type = type).toEntity()
        )

    override suspend fun updateCard(card: Card) =
        cardDao.update(card.toEntity())

    override suspend fun deleteCard(id: Long) =
        cardDao.deleteById(id)

    override suspend fun getCardById(id: Long): Card? =
        cardDao.getById(id)?.toDomain()

    override suspend fun getAllDueCards(now: Instant): List<Card> =
        cardDao.getAllDue(now.toEpochMilli()).map { it.toDomain() }

    override suspend fun getTotalDueCount(now: Instant): Int =
        cardDao.getTotalDueCount(now.toEpochMilli())

    override fun observeDueCountsByDeck(now: Instant): Flow<Map<Long, Int>> =
        cardDao.observeDueCountsByDeck(now.toEpochMilli())
            .map { counts -> counts.associate { it.deckId to it.count } }

    override suspend fun getNewCardsByDeckId(deckId: Long): List<Card> =
        cardDao.getNewByDeckId(deckId).map { it.toDomain() }

    override suspend fun getAllNewCards(): List<Card> =
        cardDao.getAllNew().map { it.toDomain() }

    override suspend fun getDueReviewCardsByDeckId(deckId: Long, now: Instant): List<Card> =
        cardDao.getDueReviewByDeckId(deckId, now.toEpochMilli()).map { it.toDomain() }

    override suspend fun getAllDueReviewCards(now: Instant): List<Card> =
        cardDao.getAllDueReview(now.toEpochMilli()).map { it.toDomain() }
}
