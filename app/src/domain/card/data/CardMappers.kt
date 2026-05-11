package io.dispersia.memly.domain.card.data

import io.dispersia.memly.domain.card.models.Card
import java.time.Instant

fun CardEntity.toDomain(): Card = Card(
    id = id,
    deckId = deckId,
    front = front,
    back = back,
    type = type,
    stability = stability,
    difficulty = difficulty,
    interval = interval,
    dueDate = Instant.ofEpochMilli(dueDate),
    lastReview = Instant.ofEpochMilli(lastReview),
    reps = reps,
    lapses = lapses,
)

fun Card.toEntity(): CardEntity = CardEntity(
    id = id,
    deckId = deckId,
    front = front,
    back = back,
    type = type,
    stability = stability,
    difficulty = difficulty,
    interval = interval,
    dueDate = dueDate.toEpochMilli(),
    lastReview = lastReview.toEpochMilli(),
    reps = reps,
    lapses = lapses,
)
