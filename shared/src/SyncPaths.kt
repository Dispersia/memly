package shared

object SyncPaths {
    const val DUE_CARDS = "/memly/due-cards"
    const val SETTINGS = "/memly/settings"
    const val DUE_COUNT = "/memly/due-count"
    const val REVIEW_RESULT = "/memly/review-result"
    const val SYNC_REQUEST = "/memly/sync-request"
}

object SyncKeys {
    const val CARDS = "cards"
    const val COUNT = "count"
    const val CARDS_PER_REVIEW = "cardsPerReview"
    const val NEW_CARDS_PER_DAY = "newCardsPerDay"
    const val ID = "id"
    const val DECK_ID = "deckId"
    const val FRONT = "front"
    const val BACK = "back"
    const val TYPE = "type"
    const val CARD_ID = "cardId"
    const val RATING = "rating"
}

object SyncDefaults {
    const val DEFAULT_CARDS_PER_REVIEW = 20
    const val DEFAULT_NEW_CARDS_PER_DAY = 10
}
