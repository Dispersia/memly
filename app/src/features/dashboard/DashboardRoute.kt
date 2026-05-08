package io.dispersia.memly.features.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.dispersia.memly.features.dashboard.presentation.DashboardIntent
import io.dispersia.memly.features.dashboard.presentation.DashboardScreen
import io.dispersia.memly.features.dashboard.presentation.DashboardViewModel
import kotlinx.serialization.Serializable

@Serializable
data object Dashboard

@Composable
fun DashboardRoute(
    viewModel: DashboardViewModel
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.dispatch(DashboardIntent.Load)
    }

    DashboardScreen(
        state = state,
        onIntent = viewModel::dispatch
    )
}
