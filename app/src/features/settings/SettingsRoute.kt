package io.dispersia.memly.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.dispersia.memly.features.settings.presentation.SettingsIntent
import io.dispersia.memly.features.settings.presentation.SettingsScreen
import io.dispersia.memly.features.settings.presentation.SettingsViewModel
import kotlinx.serialization.Serializable

@Serializable
data object Settings

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.dispatch(SettingsIntent.Load)
    }

    SettingsScreen(
        state = state,
        onIntent = viewModel::dispatch,
        onBack = onBack,
    )
}
