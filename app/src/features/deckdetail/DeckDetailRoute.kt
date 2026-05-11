package io.dispersia.memly.features.deckdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.dispersia.memly.features.deckdetail.presentation.DeckDetailEffect
import io.dispersia.memly.features.deckdetail.presentation.DeckDetailIntent
import io.dispersia.memly.features.deckdetail.presentation.DeckDetailScreen
import io.dispersia.memly.features.deckdetail.presentation.DeckDetailViewModel
import kotlinx.serialization.Serializable

@Serializable
data class DeckDetail(val deckId: Long)

@Composable
fun DeckDetailRoute(
    viewModel: DeckDetailViewModel,
    onNavigateToReview: (Long) -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.dispatch(DeckDetailIntent.Load)
        viewModel.dispatch(DeckDetailIntent.Refresh)
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is DeckDetailEffect.NavigateToReview -> onNavigateToReview(effect.deckId)
                is DeckDetailEffect.Error -> {}
            }
        }
    }

    DeckDetailScreen(
        state = state,
        onIntent = viewModel::dispatch,
        onBack = onBack,
    )
}
