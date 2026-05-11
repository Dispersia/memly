package io.dispersia.memly.features.review

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.dispersia.memly.features.review.presentation.ReviewIntent
import io.dispersia.memly.features.review.presentation.ReviewScreen
import io.dispersia.memly.features.review.presentation.ReviewViewModel
import kotlinx.serialization.Serializable

@Serializable
data class Review(val deckId: Long, val sessionId: Long = System.nanoTime())

@Composable
fun ReviewRoute(
    viewModel: ReviewViewModel,
    onFinished: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.dispatch(ReviewIntent.Load)
    }

    ReviewScreen(
        state = state,
        onIntent = viewModel::dispatch,
        onFinished = onFinished,
    )
}
