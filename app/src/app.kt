package io.dispersia.memlywear

import androidx.compose.runtime.*
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

@Composable
fun App(
    graph: AppGraph,
) {
    val backStack = remember { mutableStateListOf(Home) }

    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryProvider = entryProvider {
            entry<Home> {
                val viewModel = remember {
                    HomeViewModel(
                        repository = graph.counterRepository
                    )
                }

                HomeRoute(viewModel)
            }
        }
    )
}

@Composable
fun HomeRoute(
    viewModel: HomeViewModel
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.dispatch(HomeIntent.Load)
    }

    HomeScreen(
        state = state,
        onIntent = viewModel::dispatch
    )
}
