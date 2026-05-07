/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package io.dispersia.memlywear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.google.android.gms.wearable.Wearable
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.createGraph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {
    private val appGraph = createGraph<AppGraph>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataClient = Wearable.getDataClient(this)
        setContent {
            val counterViewModel: CounterViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        appGraph.counterViewModel
                    }
                }
            )
            CounterScreen(viewModel = counterViewModel)
        }
    }
}

data class CounterState(
    val count: Int = 0,
    val isLoading: Boolean = false
)

sealed interface CounterIntent {
    object Increment : CounterIntent
    object Decrement : CounterIntent
    object Reset : CounterIntent
}

@Inject
class CounterViewModel : ViewModel() {
    private val _state = MutableStateFlow(CounterState())
    val state: StateFlow<CounterState> = _state.asStateFlow()

    fun processIntent(intent: CounterIntent) {
        when (intent) {
            is CounterIntent.Increment -> _state.update { it.copy(count = it.count + 1) }
            is CounterIntent.Decrement -> _state.update { it.copy(count = it.count - 1) }
            is CounterIntent.Reset -> _state.update { it.copy(count = 0) }
        }
    }
}

@DependencyGraph
interface AppGraph {
    val counterViewModel: CounterViewModel
}

@Composable
fun CounterScreen(
    viewModel: CounterViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AppScaffold {
        ScreenScaffold {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Count: ${state.count}",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
