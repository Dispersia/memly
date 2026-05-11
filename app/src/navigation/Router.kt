package io.dispersia.memly.navigation

import androidx.compose.runtime.*
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import io.dispersia.memly.AppGraph
import io.dispersia.memly.features.dashboard.Dashboard
import io.dispersia.memly.features.dashboard.DashboardRoute
import io.dispersia.memly.features.dashboard.presentation.DashboardViewModel
import io.dispersia.memly.features.deckdetail.DeckDetail
import io.dispersia.memly.features.deckdetail.DeckDetailRoute
import io.dispersia.memly.features.deckdetail.presentation.DeckDetailViewModel
import io.dispersia.memly.features.review.Review
import io.dispersia.memly.features.review.ReviewRoute
import io.dispersia.memly.features.review.presentation.ReviewViewModel
import io.dispersia.memly.features.settings.Settings
import io.dispersia.memly.features.settings.SettingsRoute
import io.dispersia.memly.features.settings.presentation.SettingsViewModel

@Composable
fun Router(
    graph: AppGraph,
) {
    val backStack = remember { mutableStateListOf<Any>(Dashboard) }

    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryProvider = entryProvider {
            entry<Dashboard> {
                val viewModel = remember {
                    DashboardViewModel(
                        deckRepository = graph.deckRepository,
                        cardRepository = graph.cardRepository,
                    )
                }

                DashboardRoute(
                    viewModel = viewModel,
                    onNavigateToDeck = { deckId -> backStack.add(DeckDetail(deckId)) },
                    onNavigateToSettings = { backStack.add(Settings) },
                )
            }

            entry<DeckDetail> { route ->
                val viewModel = remember {
                    DeckDetailViewModel(
                        deckId = route.deckId,
                        deckRepository = graph.deckRepository,
                        cardRepository = graph.cardRepository,
                        wearDataManager = graph.wearDataManager,
                    )
                }

                DeckDetailRoute(
                    viewModel = viewModel,
                    onNavigateToReview = { deckId -> backStack.add(Review(deckId)) },
                    onBack = { backStack.removeLastOrNull() },
                )
            }

            entry<Review> { route ->
                val viewModel = remember {
                    ReviewViewModel(
                        deckId = route.deckId,
                        cardRepository = graph.cardRepository,
                        settingsRepository = graph.settingsRepository,
                    )
                }

                ReviewRoute(
                    viewModel = viewModel,
                    onFinished = { backStack.removeLastOrNull() },
                )
            }

            entry<Settings> {
                val viewModel = remember {
                    SettingsViewModel(
                        settingsRepository = graph.settingsRepository,
                        wearDataManager = graph.wearDataManager,
                    )
                }

                SettingsRoute(
                    viewModel = viewModel,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        }
    )
}
