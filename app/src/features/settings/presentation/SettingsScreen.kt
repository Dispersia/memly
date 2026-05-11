@file:OptIn(ExperimentalMaterial3Api::class)

package io.dispersia.memly.features.settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SettingStepper(
                title = "Cards per review",
                description = "How many cards to review every 30 minutes",
                value = state.cardsPerReview,
                step = 5,
                range = 5..100,
                onValueChange = { onIntent(SettingsIntent.UpdateCardsPerReview(it)) },
            )
            SettingStepper(
                title = "New cards per day",
                description = "How many unseen cards to introduce each day",
                value = state.newCardsPerDay,
                step = 1,
                range = 0..50,
                onValueChange = { onIntent(SettingsIntent.UpdateNewCardsPerDay(it)) },
            )
        }
    }
}

@Composable
private fun SettingStepper(
    title: String,
    description: String,
    value: Int,
    step: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                FilledTonalIconButton(
                    onClick = {
                        val newValue = value - step
                        if (newValue >= range.first) onValueChange(newValue)
                    },
                    enabled = value > range.first,
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    "$value",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(modifier = Modifier.width(24.dp))
                FilledTonalIconButton(
                    onClick = {
                        val newValue = value + step
                        if (newValue <= range.last) onValueChange(newValue)
                    },
                    enabled = value < range.last,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }
        }
    }
}
