package io.dispersia.memlywear

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.*

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
) {
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.loading) {
            item {
                CircularProgressIndicator()
            }
        }

        item {
            Text("Count: ${state.count}")
        }

        item {
            Button(
                onClick = {
                    onIntent(HomeIntent.Increment)
                }
            ) {
                Text("Increment")
            }
        }

        item {
            Button(
                onClick = {
                    onIntent(HomeIntent.Load)
                }
            ) {
                Text("Reset")
            }
        }
    }
}
