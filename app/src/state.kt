package io.dispersia.memly

data class HomeState(
    val loading: Boolean = false,
    val count: Int = 0,
)

sealed interface HomeIntent {
    data object Increment : HomeIntent
    data object Load : HomeIntent
}

sealed interface HomeEffect {
    data class Error(
        val message: String
    ) : HomeEffect
}
