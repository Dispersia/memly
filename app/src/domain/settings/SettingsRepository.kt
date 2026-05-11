package io.dispersia.memly.domain.settings

import android.app.Application
import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import shared.SyncDefaults
import java.time.LocalDate

interface SettingsRepository {
    fun observeCardsPerReview(): Flow<Int>
    suspend fun getCardsPerReview(): Int
    suspend fun setCardsPerReview(count: Int)
    fun observeNewCardsPerDay(): Flow<Int>
    suspend fun getNewCardsPerDay(): Int
    suspend fun setNewCardsPerDay(count: Int)
    suspend fun getNewCardsShownToday(): Int
    suspend fun incrementNewCardsShownToday(count: Int)
}

@ContributesBinding(AppScope::class)
@Inject
class SettingsRepositoryImpl(
    private val application: Application,
) : SettingsRepository {
    private val prefs by lazy {
        application.getSharedPreferences("memly_settings", Context.MODE_PRIVATE)
    }

    override fun observeCardsPerReview(): Flow<Int> = callbackFlow {
        trySend(getCardsPerReviewSync())
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_CARDS_PER_REVIEW) {
                trySend(getCardsPerReviewSync())
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun getCardsPerReview(): Int = getCardsPerReviewSync()

    override suspend fun setCardsPerReview(count: Int) {
        prefs.edit().putInt(KEY_CARDS_PER_REVIEW, count).apply()
    }

    override fun observeNewCardsPerDay(): Flow<Int> = callbackFlow {
        trySend(getNewCardsPerDaySync())
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_NEW_CARDS_PER_DAY) {
                trySend(getNewCardsPerDaySync())
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun getNewCardsPerDay(): Int = getNewCardsPerDaySync()

    override suspend fun setNewCardsPerDay(count: Int) {
        prefs.edit().putInt(KEY_NEW_CARDS_PER_DAY, count).apply()
    }

    override suspend fun getNewCardsShownToday(): Int {
        val today = LocalDate.now().toString()
        val storedDate = prefs.getString(KEY_NEW_CARDS_DATE, null)
        if (storedDate != today) return 0
        return prefs.getInt(KEY_NEW_CARDS_COUNT, 0)
    }

    override suspend fun incrementNewCardsShownToday(count: Int) {
        val today = LocalDate.now().toString()
        val storedDate = prefs.getString(KEY_NEW_CARDS_DATE, null)
        val current = if (storedDate == today) prefs.getInt(KEY_NEW_CARDS_COUNT, 0) else 0
        prefs.edit()
            .putString(KEY_NEW_CARDS_DATE, today)
            .putInt(KEY_NEW_CARDS_COUNT, current + count)
            .apply()
    }

    private fun getCardsPerReviewSync(): Int {
        val value = prefs.getInt(KEY_CARDS_PER_REVIEW, -1)
        if (value != -1) return value
        return prefs.getInt(KEY_CARDS_PER_SESSION_LEGACY, SyncDefaults.DEFAULT_CARDS_PER_REVIEW)
    }

    private fun getNewCardsPerDaySync(): Int =
        prefs.getInt(KEY_NEW_CARDS_PER_DAY, SyncDefaults.DEFAULT_NEW_CARDS_PER_DAY)

    companion object {
        private const val KEY_CARDS_PER_REVIEW = "cards_per_review"
        private const val KEY_CARDS_PER_SESSION_LEGACY = "cards_per_session"
        private const val KEY_NEW_CARDS_PER_DAY = "new_cards_per_day"
        private const val KEY_NEW_CARDS_DATE = "new_cards_shown_date"
        private const val KEY_NEW_CARDS_COUNT = "new_cards_shown_count"
    }
}
