package io.dispersia.memlywear

data class HomeState(
    val loading: Boolean = false,
    val dueCount: Int = 0,
)

sealed interface HomeIntent {
    data object Load : HomeIntent
}

sealed interface HomeEffect {
    data class Error(val message: String) : HomeEffect
}
