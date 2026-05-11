package io.dispersia.memly.domain.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.dispersia.memly.domain.card.data.CardDao
import io.dispersia.memly.domain.card.data.CardEntity
import io.dispersia.memly.domain.card.data.Converters
import io.dispersia.memly.domain.deck.data.DeckDao
import io.dispersia.memly.domain.deck.data.DeckEntity

@Database(
    entities = [DeckEntity::class, CardEntity::class],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class MemlyDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao
}
