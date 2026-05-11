package io.dispersia.memly.domain.card.data

import androidx.room.TypeConverter
import io.dispersia.memly.domain.card.models.CardType

class Converters {
    @TypeConverter
    fun fromCardType(type: CardType): String = type.name

    @TypeConverter
    fun toCardType(value: String): CardType = CardType.valueOf(value)
}
