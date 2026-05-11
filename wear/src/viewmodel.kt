package io.dispersia.memlywear

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shared.SyncPaths

class HomeViewModel(
    private val application: Application,
    private val repository: WearCardRepository,
) : ViewModel(), DataClient.OnDataChangedListener {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HomeEffect>()
    val effects = _effects.asSharedFlow()

    private val dataClient by lazy { Wearable.getDataClient(application) }

    init {
        dataClient.addListener(this)
    }

    fun dispatch(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Load -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            runCatching { repository.getDueCount() }
                .onSuccess { count ->
                    Log.d(TAG, "load: dueCount=$count")
                    _state.update { it.copy(loading = false, dueCount = count) }
                }
                .onFailure { e ->
                    Log.e(TAG, "load failed", e)
                    _state.update { it.copy(loading = false) }
                    _effects.emit(HomeEffect.Error("Failed to load"))
                }
        }
    }

    override fun onDataChanged(events: DataEventBuffer) {
        for (event in events) {
            Log.d(TAG, "onDataChanged: type=${event.type} path=${event.dataItem.uri.path}")
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path ?: continue
                if (path == SyncPaths.DUE_COUNT || path == SyncPaths.DUE_CARDS) {
                    load()
                    return
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        dataClient.removeListener(this)
    }

    companion object {
        private const val TAG = "WearHome"
    }
}
