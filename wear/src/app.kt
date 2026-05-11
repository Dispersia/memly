package io.dispersia.memlywear

import android.app.Application
import androidx.compose.runtime.*
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import io.dispersia.memlywear.review.WearReviewIntent
import io.dispersia.memlywear.review.WearReviewScreen
import io.dispersia.memlywear.review.WearReviewViewModel

@Composable
fun App(
    graph: AppGraph,
    application: Application,
) {
    val backStack = remember { mutableStateListOf<Any>(Home) }

    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryProvider = entryProvider {
            entry<Home> {
                val viewModel = remember {
                    HomeViewModel(
                        application = application,
                        repository = graph.wearCardRepository,
                    )
                }

                val state by viewModel.state.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.dispatch(HomeIntent.Load)
                }

                HomeScreen(
                    state = state,
                    onIntent = viewModel::dispatch,
                    onStartReview = { backStack.add(WearReview) },
                )
            }

            entry<WearReview> {
                val viewModel = remember {
                    WearReviewViewModel(
                        application = application,
                        repository = graph.wearCardRepository,
                        pendingResultStore = graph.wearPendingResultStore,
                    )
                }

                val state by viewModel.state.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.dispatch(WearReviewIntent.Load)
                }

                WearReviewScreen(
                    state = state,
                    onIntent = viewModel::dispatch,
                    onFinished = { backStack.removeLastOrNull() },
                )
            }
        }
    )
}
