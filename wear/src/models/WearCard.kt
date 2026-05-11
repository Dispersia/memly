package io.dispersia.memlywear.models

enum class WearCardType {
    SelfAssessed,
    TypeIn,
}

data class WearCard(
    val id: Long,
    val deckId: Long,
    val front: String,
    val back: String,
    val type: WearCardType,
)
