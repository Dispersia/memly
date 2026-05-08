package io.dispersia.memly

import android.app.Application
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class App : Application(), MetroApplication {
    val appGraph by lazy { createGraph<AppGraph>() }

    override val appComponentProviders: MetroAppComponentProviders
        get() = appGraph
}
