package io.dispersia.memlywear.offline

import android.app.Application
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import org.json.JSONArray
import org.json.JSONObject

data class PendingResult(
    val cardId: Long,
    val ratingValue: Int,
    val timestampMillis: Long,
)

interface WearPendingResultStore {
    fun save(result: PendingResult)
    fun getAll(): List<PendingResult>
    fun clear()
    fun getAnsweredCardIds(): Set<Long>
}

@ContributesBinding(AppScope::class)
@Inject
class WearPendingResultStoreImpl(
    private val application: Application,
) : WearPendingResultStore {
    private val prefs by lazy {
        application.getSharedPreferences("memly_pending_results", android.content.Context.MODE_PRIVATE)
    }

    override fun save(result: PendingResult) {
        val current = loadJson()
        val obj = JSONObject().apply {
            put("cardId", result.cardId)
            put("rating", result.ratingValue)
            put("timestamp", result.timestampMillis)
        }
        current.put(obj)
        prefs.edit().putString(KEY_RESULTS, current.toString()).apply()
    }

    override fun getAll(): List<PendingResult> {
        val arr = loadJson()
        return (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            PendingResult(
                cardId = obj.getLong("cardId"),
                ratingValue = obj.getInt("rating"),
                timestampMillis = obj.getLong("timestamp"),
            )
        }
    }

    override fun clear() {
        prefs.edit().remove(KEY_RESULTS).apply()
    }

    override fun getAnsweredCardIds(): Set<Long> {
        val arr = loadJson()
        return (0 until arr.length()).map { arr.getJSONObject(it).getLong("cardId") }.toSet()
    }

    private fun loadJson(): JSONArray {
        val raw = prefs.getString(KEY_RESULTS, null) ?: return JSONArray()
        return runCatching { JSONArray(raw) }.getOrDefault(JSONArray())
    }

    companion object {
        private const val KEY_RESULTS = "pending_results"
    }
}
