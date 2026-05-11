package io.dispersia.memlywear.review

import android.app.Activity
import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.*
import androidx.wear.input.RemoteInputIntentHelper
import io.dispersia.memlywear.models.WearCardType

private const val REMOTE_INPUT_KEY = "answer"

@Composable
fun WearReviewScreen(
    state: WearReviewState,
    onIntent: (WearReviewIntent) -> Unit,
    onFinished: () -> Unit,
) {
    when {
        state.loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.isSessionComplete -> SessionSummary(state, onFinished)
        state.currentCard != null && !state.isRevealed -> {
            val card = state.currentCard!!
            when (card.type) {
                WearCardType.SelfAssessed -> SelfAssessedCard(state, onIntent)
                WearCardType.TypeIn -> TypeInCard(state, onIntent)
            }
        }
        state.currentCard != null && state.isRevealed -> {
            RevealedCard(state, onIntent)
        }
    }
}

@Composable
private fun SelfAssessedCard(
    state: WearReviewState,
    onIntent: (WearReviewIntent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape),
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                    .clickable { onIntent(WearReviewIntent.MarkIncorrect) },
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.outlineVariant),
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f))
                    .clickable { onIntent(WearReviewIntent.MarkCorrect) },
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "${state.currentIndex + 1} / ${state.totalCards}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = state.currentCard!!.front,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 32.dp),
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    text = "✗",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error,
                )
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}

@Composable
private fun TypeInCard(
    state: WearReviewState,
    onIntent: (WearReviewIntent) -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val answer = RemoteInput.getResultsFromIntent(result.data)
                ?.getCharSequence(REMOTE_INPUT_KEY)?.toString() ?: ""
            onIntent(WearReviewIntent.UpdateTypedAnswer(answer))
            onIntent(WearReviewIntent.SubmitTypedAnswer)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            Text(
                text = "${state.currentIndex + 1} / ${state.totalCards}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = state.currentCard!!.front,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(16.dp))
            FilledTonalButton(
                onClick = {
                    val remoteInputs = listOf(
                        RemoteInput.Builder(REMOTE_INPUT_KEY)
                            .setLabel("Your answer")
                            .build()
                    )
                    val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                    RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                    launcher.launch(intent)
                },
            ) {
                Text("Answer")
            }
        }
    }
}

@Composable
private fun RevealedCard(
    state: WearReviewState,
    onIntent: (WearReviewIntent) -> Unit,
) {
    val isCorrect = state.answerResult == AnswerResult.Correct
    val containerColor = if (isCorrect) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = if (isCorrect) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(containerColor)
            .clickable { onIntent(WearReviewIntent.NextCard) },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = if (isCorrect) "✓" else "✗",
                style = MaterialTheme.typography.displaySmall,
                color = contentColor,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.currentCard!!.back,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = contentColor,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tap to continue",
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun SessionSummary(
    state: WearReviewState,
    onFinished: () -> Unit,
) {
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                text = "Session Complete",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        item {
            Text(
                text = "${state.correctCount + state.incorrectCount}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        item {
            Text(
                text = "cards reviewed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.correctCount}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                    Text(
                        text = "correct",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.incorrectCount}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = "wrong",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Button(onClick = onFinished) {
                Text("Done")
            }
        }
    }
}
