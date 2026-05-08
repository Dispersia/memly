package io.dispersia.memly

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

interface CounterRepository {
    suspend fun loadInitialCount(): Int
}

@ContributesBinding(AppScope::class)
@Inject
class CounterRepositoryImpl : CounterRepository {
    override suspend fun loadInitialCount(): Int {
        return 5
    }
}

