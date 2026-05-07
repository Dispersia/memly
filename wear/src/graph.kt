package io.dispersia.memlywear

import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph
interface AppGraph {
    val counterRepository: CounterRepository

    companion object
}

object AppModule {
    @Provides
    fun provideCounterRepository(): CounterRepository {
        return CounterRepositoryImpl()
    }
}
