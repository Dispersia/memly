/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package io.dispersia.memlywear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.gms.wearable.Wearable
import dev.zacsweers.metro.createGraph
import io.dispersia.memlywear.App
import io.dispersia.memlywear.AppGraph
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val graph: AppGraph
        get() = (application as App).appGraph

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataClient = Wearable.getDataClient(this)
        setContent {
            App(graph = graph)
        }
    }
}

