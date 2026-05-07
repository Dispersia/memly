package io.dispersia.memlywear

interface CounterRepository {
    suspend fun loadInitialCount(): Int
}

class CounterRepositoryImpl : CounterRepository {
    override suspend fun loadInitialCount(): Int {
        return 5
    }
}

