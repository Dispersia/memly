package io.dispersia.memly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.dispersia.memly.core.presentation.theme.MemlyTheme
import io.dispersia.memly.navigation.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val graph: AppGraph
        get() = (application as App).appGraph

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                graph.wearDataManager.syncDueCardsToWatch()
                graph.wearDataManager.syncSettingsToWatch()
            }
        }

        setContent {
            MemlyTheme {
                Router(graph = graph)
            }
        }
    }
}
