package io.dispersia.memlywear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: CounterRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HomeEffect>()
    val effects = _effects.asSharedFlow()

    fun dispatch(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Increment -> {
                _state.update { it.copy(count = it.count + 1) }
            }

            HomeIntent.Load -> {
                load()
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update {
                it.copy(loading = true)
            }

            runCatching {
                repository.loadInitialCount()
            }.onSuccess { count ->
                _state.update {
                    it.copy(
                        loading = false,
                        count = count
                    )
                }
            }.onFailure {
                _state.update {
                    it.copy(loading = false)
                }

                _effects.emit(
                    HomeEffect.Error("Failed to load")
                )
            }
        }
    }
}
