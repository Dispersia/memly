package io.dispersia.memly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.wearable.Wearable
import io.dispersia.memly.core.presentation.theme.MemlyTheme
import io.dispersia.memly.navigation.Router

class MainActivity : ComponentActivity() {
    private val graph: AppGraph
        get() = (application as App).appGraph

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        val dataClient = Wearable.getDataClient(this)

        setContent {
            MemlyTheme {
                Router(graph = graph)
            }
        }
    }
}

