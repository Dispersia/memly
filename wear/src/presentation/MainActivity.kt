package io.dispersia.memlywear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.dispersia.memlywear.App
import io.dispersia.memlywear.AppGraph
import io.dispersia.memlywear.presentation.theme.MemlyTheme

class MainActivity : ComponentActivity() {
    private val graph: AppGraph
        get() = (application as App).appGraph

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            MemlyTheme {
                io.dispersia.memlywear.App(
                    graph = graph,
                    application = application as App,
                )
            }
        }
    }
}
