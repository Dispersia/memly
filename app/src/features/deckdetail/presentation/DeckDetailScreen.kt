@file:OptIn(ExperimentalMaterial3Api::class)

package io.dispersia.memly.features.deckdetail.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.dispersia.memly.domain.card.models.Card
import io.dispersia.memly.domain.card.models.CardType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun DeckDetailScreen(
    state: DeckDetailState,
    onIntent: (DeckDetailIntent) -> Unit,
    onBack: () -> Unit,
) {
    val filteredCards = if (state.searchQuery.isBlank()) {
        state.cards
    } else {
        val query = state.searchQuery.lowercase()
        state.cards.filter { card ->
            card.front.lowercase().contains(query) || card.back.lowercase().contains(query)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.deck?.name ?: "Deck") },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(DeckDetailIntent.ShowAddCardDialog) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (state.cards.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            "${state.cards.size} cards",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            "${state.dueCount} due",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    FilledTonalButton(
                        onClick = { onIntent(DeckDetailIntent.StartReview) },
                        enabled = state.dueCount > 0,
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Study (${state.dueCount})")
                    }
                }

                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onIntent(DeckDetailIntent.UpdateSearchQuery(it)) },
                    placeholder = { Text("Search cards") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onIntent(DeckDetailIntent.UpdateSearchQuery("")) }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                )

                HorizontalDivider()
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = filteredCards,
                    key = { it.id },
                ) { card ->
                    CardItem(
                        card = card,
                        onClick = { onIntent(DeckDetailIntent.EditCard(card)) },
                        onDelete = { onIntent(DeckDetailIntent.DeleteCard(card.id)) },
                    )
                }
            }
        }

        if (state.showAddCardDialog) {
            CardDialog(
                title = "Add Card",
                front = state.newCardFront,
                back = state.newCardBack,
                cardType = state.newCardType,
                confirmLabel = "Add",
                onFrontChange = { onIntent(DeckDetailIntent.UpdateNewCardFront(it)) },
                onBackChange = { onIntent(DeckDetailIntent.UpdateNewCardBack(it)) },
                onTypeChange = { onIntent(DeckDetailIntent.UpdateNewCardType(it)) },
                onConfirm = { onIntent(DeckDetailIntent.ConfirmAddCard) },
                onDismiss = { onIntent(DeckDetailIntent.DismissAddCardDialog) },
            )
        }

        if (state.showEditCardDialog) {
            CardDialog(
                title = "Edit Card",
                front = state.editCardFront,
                back = state.editCardBack,
                cardType = state.editCardType,
                confirmLabel = "Save",
                onFrontChange = { onIntent(DeckDetailIntent.UpdateEditCardFront(it)) },
                onBackChange = { onIntent(DeckDetailIntent.UpdateEditCardBack(it)) },
                onTypeChange = { onIntent(DeckDetailIntent.UpdateEditCardType(it)) },
                onConfirm = { onIntent(DeckDetailIntent.ConfirmEditCard) },
                onDismiss = { onIntent(DeckDetailIntent.DismissEditCardDialog) },
            )
        }
    }
}

@Composable
private fun CardItem(
    card: Card,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val isDue = card.dueDate.isBefore(Instant.now())

    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = card.front,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                ) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = card.back,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isDue)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = formatDueDate(card.dueDate),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDue)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            when (card.type) {
                                CardType.SelfAssessed -> "Self-Assessed"
                                CardType.TypeIn -> "Type In"
                            },
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    modifier = Modifier.height(24.dp),
                )
            }
        }
    }
}

private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, h:mm a")

private fun formatDueDate(dueDate: Instant): String {
    val now = Instant.now()
    if (dueDate.isBefore(now)) return "Due now"

    val minutes = ChronoUnit.MINUTES.between(now, dueDate)
    if (minutes < 60) return "Due in ${minutes}m"

    val hours = ChronoUnit.HOURS.between(now, dueDate)
    if (hours < 24) return "Due in ${hours}h"

    val days = ChronoUnit.DAYS.between(now, dueDate)
    if (days < 7) return "Due in ${days}d"

    return "Due ${dateFormatter.format(dueDate.atZone(ZoneId.systemDefault()))}"
}

@Composable
private fun CardDialog(
    title: String,
    front: String,
    back: String,
    cardType: CardType,
    confirmLabel: String,
    onFrontChange: (String) -> Unit,
    onBackChange: (String) -> Unit,
    onTypeChange: (CardType) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = front,
                    onValueChange = onFrontChange,
                    label = { Text("Front") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = back,
                    onValueChange = onBackChange,
                    label = { Text("Back") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    CardType.entries.forEachIndexed { index, type ->
                        SegmentedButton(
                            selected = cardType == type,
                            onClick = { onTypeChange(type) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = CardType.entries.size,
                            ),
                        ) {
                            Text(
                                when (type) {
                                    CardType.SelfAssessed -> "Self-Assessed"
                                    CardType.TypeIn -> "Type In"
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = front.isNotBlank() && back.isNotBlank(),
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
