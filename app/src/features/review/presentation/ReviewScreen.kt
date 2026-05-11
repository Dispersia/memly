@file:OptIn(ExperimentalMaterial3Api::class)

package io.dispersia.memly.features.review.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.dispersia.memly.domain.card.models.CardType

@Composable
fun ReviewScreen(
    state: ReviewState,
    onIntent: (ReviewIntent) -> Unit,
    onFinished: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (!state.isSessionComplete && state.totalCards > 0) {
                        Text("${state.currentIndex + 1} / ${state.totalCards}")
                    } else {
                        Text("Review")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            if (!state.isSessionComplete && state.totalCards > 0) {
                LinearProgressIndicator(
                    progress = { (state.currentIndex.toFloat()) / state.totalCards },
                    modifier = Modifier.fillMaxWidth(),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, fill = true),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    state.loading -> CircularProgressIndicator()
                    state.isSessionComplete -> SessionSummary(state, onFinished)
                    state.currentCard != null -> {
                        when (state.currentCard!!.type) {
                            CardType.SelfAssessed -> SelfAssessedCard(state, onIntent)
                            CardType.TypeIn -> TypeInCard(state, onIntent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlashCard(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
) {
    val animatedColor by animateColorAsState(targetValue = containerColor, label = "cardColor")

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = animatedColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 160.dp)
                .padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SelfAssessedCard(
    state: ReviewState,
    onIntent: (ReviewIntent) -> Unit,
) {
    val card = state.currentCard ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        FlashCard(text = card.front)

        Spacer(modifier = Modifier.height(20.dp))

        if (state.isRevealed) {
            FlashCard(
                text = card.back,
                containerColor = when (state.answerResult) {
                    AnswerResult.Correct -> MaterialTheme.colorScheme.tertiaryContainer
                    AnswerResult.Incorrect -> MaterialTheme.colorScheme.errorContainer
                    null -> MaterialTheme.colorScheme.surface
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onIntent(ReviewIntent.NextCard) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Next")
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = { onIntent(ReviewIntent.MarkIncorrect) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Don't Know")
                }
                Button(
                    onClick = { onIntent(ReviewIntent.MarkCorrect) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary,
                    ),
                ) {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Know It")
                }
            }
        }
    }
}

@Composable
private fun TypeInCard(
    state: ReviewState,
    onIntent: (ReviewIntent) -> Unit,
) {
    val card = state.currentCard ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        FlashCard(text = card.front)

        Spacer(modifier = Modifier.height(20.dp))

        if (state.isRevealed) {
            FlashCard(
                text = buildString {
                    if (state.answerResult == AnswerResult.Correct) {
                        append("Correct!")
                    } else {
                        append("Incorrect\n")
                        append(card.back)
                    }
                },
                containerColor = when (state.answerResult) {
                    AnswerResult.Correct -> MaterialTheme.colorScheme.tertiaryContainer
                    AnswerResult.Incorrect -> MaterialTheme.colorScheme.errorContainer
                    null -> MaterialTheme.colorScheme.surface
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onIntent(ReviewIntent.NextCard) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Next")
            }
        } else {
            OutlinedTextField(
                value = state.typedAnswer,
                onValueChange = { onIntent(ReviewIntent.UpdateTypedAnswer(it)) },
                label = { Text("Your answer") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onIntent(ReviewIntent.SubmitTypedAnswer) },
                enabled = state.typedAnswer.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Submit")
            }
        }
    }
}

@Composable
private fun SessionSummary(
    state: ReviewState,
    onFinished: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.padding(24.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Session Complete",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${state.correctCount}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                    Text(
                        "correct",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${state.incorrectCount}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        "incorrect",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (state.totalCards > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "out of ${state.totalCards} cards",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            FilledTonalButton(
                onClick = onFinished,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Done")
            }
        }
    }
}
