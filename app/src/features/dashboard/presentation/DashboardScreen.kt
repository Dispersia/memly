@file:OptIn(ExperimentalMaterial3Api::class)

package io.dispersia.memly.features.dashboard.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    state: DashboardState,
    onIntent: (DashboardIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Memly") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                actions = {
                    IconButton(onClick = { onIntent(DashboardIntent.OpenSettings) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(DashboardIntent.ShowCreateDeckDialog) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Deck")
            }
        }
    ) { innerPadding ->
        if (state.decks.isEmpty() && !state.loading) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No decks yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap + to create your first deck",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = state.decks,
                    key = { it.id },
                ) { deck ->
                    val dueCount = state.dueCounts[deck.id] ?: 0
                    ElevatedCard(
                        onClick = { onIntent(DashboardIntent.DeckClicked(deck.id)) },
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    deck.name,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            },
                            supportingContent = {
                                if (dueCount > 0) {
                                    Text(
                                        "$dueCount due",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                } else {
                                    Text(
                                        "All caught up",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            },
                            leadingContent = {
                                Icon(
                                    Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = if (dueCount > 0)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            trailingContent = {
                                if (dueCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ) {
                                        Text("$dueCount")
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }

        if (state.showCreateDeckDialog) {
            AlertDialog(
                onDismissRequest = { onIntent(DashboardIntent.DismissCreateDeckDialog) },
                title = { Text("Create Deck") },
                text = {
                    OutlinedTextField(
                        value = state.newDeckName,
                        onValueChange = { onIntent(DashboardIntent.UpdateNewDeckName(it)) },
                        label = { Text("Deck name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { onIntent(DashboardIntent.ConfirmCreateDeck) },
                        enabled = state.newDeckName.isNotBlank(),
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onIntent(DashboardIntent.DismissCreateDeckDialog) }) {
                        Text("Cancel")
                    }
                },
            )
        }
    }
}
