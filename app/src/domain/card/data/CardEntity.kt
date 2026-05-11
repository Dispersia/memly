package io.dispersia.memly.domain.card.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.dispersia.memly.domain.card.models.CardType
import io.dispersia.memly.domain.deck.data.DeckEntity

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("deckId")],
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val front: String,
    val back: String,
    val type: CardType,
    val stability: Double = 0.0,
    val difficulty: Double = 0.0,
    val interval: Int = 0,
    val dueDate: Long = 0,
    val lastReview: Long = 0,
    val reps: Int = 0,
    val lapses: Int = 0,
)
