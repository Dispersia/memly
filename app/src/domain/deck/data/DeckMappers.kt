package io.dispersia.memly.domain.deck.data

import io.dispersia.memly.domain.deck.models.Deck

fun DeckEntity.toDomain(): Deck = Deck(id = id, name = name)

fun Deck.toEntity(): DeckEntity = DeckEntity(id = id, name = name)
