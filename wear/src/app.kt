package io.dispersia.memlywear

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

@Composable
fun App(
    graph: AppGraph,
) {
    val backStack = rememberNavBackStack(Home)

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<Home> {
                val factory = remember(graph) {
                    HomeViewModelFactory(graph)
                }

                val viewModel: HomeViewModel = viewModel(
                    factory = factory
                )

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
