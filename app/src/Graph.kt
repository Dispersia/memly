package io.dispersia.memly

import android.app.Application
import androidx.room.Room
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import io.dispersia.memly.domain.card.CardRepository
import io.dispersia.memly.domain.card.data.CardDao
import io.dispersia.memly.domain.data.MemlyDatabase
import io.dispersia.memly.domain.deck.DeckRepository
import io.dispersia.memly.domain.deck.data.DeckDao
import io.dispersia.memly.domain.settings.SettingsRepository
import io.dispersia.memly.domain.sync.WearDataManager

@DependencyGraph(AppScope::class)
interface AppGraph : MetroAppComponentProviders {
    val deckRepository: DeckRepository
    val cardRepository: CardRepository
    val settingsRepository: SettingsRepository
    val wearDataManager: WearDataManager

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AppGraph
    }
}

@ContributesTo(AppScope::class)
@BindingContainer
object DatabaseModule {
    @Provides
    fun provideDatabase(application: Application): MemlyDatabase =
        Room.databaseBuilder(
            application,
            MemlyDatabase::class.java,
            "memly-database",
        ).build()

    @Provides
    fun provideDeckDao(database: MemlyDatabase): DeckDao =
        database.deckDao()

    @Provides
    fun provideCardDao(database: MemlyDatabase): CardDao =
        database.cardDao()
}
