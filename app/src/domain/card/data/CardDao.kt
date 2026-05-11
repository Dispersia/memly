package io.dispersia.memly.domain.card.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE deckId = :deckId")
    fun observeByDeckId(deckId: Long): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND dueDate <= :nowEpochMilli")
    suspend fun getDueByDeckId(deckId: Long, nowEpochMilli: Long): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE deckId = :deckId AND dueDate <= :nowEpochMilli")
    fun observeDueCount(deckId: Long, nowEpochMilli: Long): Flow<Int>

    @Insert
    suspend fun insert(card: CardEntity): Long

    @Update
    suspend fun update(card: CardEntity)

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getById(id: Long): CardEntity?

    @Query("SELECT * FROM cards WHERE dueDate <= :nowEpochMilli")
    suspend fun getAllDue(nowEpochMilli: Long): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE dueDate <= :nowEpochMilli")
    suspend fun getTotalDueCount(nowEpochMilli: Long): Int

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT deckId, COUNT(*) as count FROM cards WHERE dueDate <= :nowEpochMilli GROUP BY deckId")
    fun observeDueCountsByDeck(nowEpochMilli: Long): Flow<List<DeckDueCount>>

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND reps = 0")
    suspend fun getNewByDeckId(deckId: Long): List<CardEntity>

    @Query("SELECT * FROM cards WHERE reps = 0")
    suspend fun getAllNew(): List<CardEntity>

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND reps > 0 AND dueDate <= :nowEpochMilli")
    suspend fun getDueReviewByDeckId(deckId: Long, nowEpochMilli: Long): List<CardEntity>

    @Query("SELECT * FROM cards WHERE reps > 0 AND dueDate <= :nowEpochMilli")
    suspend fun getAllDueReview(nowEpochMilli: Long): List<CardEntity>
}

data class DeckDueCount(
    val deckId: Long,
    val count: Int,
)
