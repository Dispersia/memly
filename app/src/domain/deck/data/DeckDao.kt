package io.dispersia.memly.domain.deck.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks")
    fun observeAll(): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun getById(id: Long): DeckEntity?

    @Insert
    suspend fun insert(deck: DeckEntity): Long

    @Query("DELETE FROM decks WHERE id = :id")
    suspend fun deleteById(id: Long)
}
