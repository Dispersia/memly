package io.dispersia.memly.domain.card.models

import java.time.Instant

data class Card(
    val id: Long = 0,
    val deckId: Long,
    val front: String,
    val back: String,
    val type: CardType,
    val stability: Double = 0.0,
    val difficulty: Double = 0.0,
    val interval: Int = 0,
    val dueDate: Instant = Instant.now(),
    val lastReview: Instant = Instant.now(),
    val reps: Int = 0,
    val lapses: Int = 0,
)
