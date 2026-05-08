package io.dispersia.memly

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.*

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
) {
    Column {
        if (state.loading) {
            CircularProgressIndicator()
        }

        Text("Count: ${state.count}")

        Button(
            onClick = {
                onIntent(HomeIntent.Increment)
            }
        ) {
            Text("Increment")
        }

        Button(
            onClick = {
                onIntent(HomeIntent.Load)
            }
        ) {
            Text("Reset")
        }
    }
}
