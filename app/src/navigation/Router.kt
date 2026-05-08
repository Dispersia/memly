package io.dispersia.memly.navigation

import androidx.compose.runtime.*
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import io.dispersia.memly.AppGraph
import io.dispersia.memly.features.dashboard.Dashboard
import io.dispersia.memly.features.dashboard.DashboardRoute
import io.dispersia.memly.features.dashboard.presentation.DashboardScreen
import io.dispersia.memly.features.dashboard.presentation.DashboardViewModel

@Composable
fun Router(
    graph: AppGraph,
) {
    val backStack = remember { mutableStateListOf(Dashboard) }

    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryProvider = entryProvider {
            entry<Dashboard> {
                val viewModel = remember {
                    DashboardViewModel(
                        deckRepository = graph.deckRepository
                    )
                }

                DashboardRoute(viewModel)
            }
        }
    )
}

