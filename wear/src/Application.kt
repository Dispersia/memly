package io.dispersia.memlywear

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication
import io.dispersia.memlywear.workers.ReviewWorker
import java.util.concurrent.TimeUnit

class App : Application(), MetroApplication {
    val appGraph by lazy { createGraph<AppGraph>() }

    override val appComponentProviders: MetroAppComponentProviders
        get() = appGraph

    override fun onCreate() {
        super.onCreate()

        val request =
            PeriodicWorkRequestBuilder<ReviewWorker>(
                30,
                TimeUnit.MINUTES
            ).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "review-worker",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }
}
