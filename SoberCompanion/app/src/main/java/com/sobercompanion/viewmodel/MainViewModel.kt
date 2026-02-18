package com.sobercompanion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sobercompanion.data.AppDataStore
import com.sobercompanion.data.RecordStatus
import com.sobercompanion.data.SoberRepository
import com.sobercompanion.workers.ComfortMessageWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

data class MainUiState(
    val startDate: LocalDate? = null,
    val currentStreak: Int = 0,
    val dailyStatus: RecordStatus = RecordStatus.SUCCESS,
    val shakyCountToday: Int = 0,
    val shakyTimestamps: List<LocalDateTime> = emptyList(),
    val lastRecordDate: LocalDate? = null,
    val shakyTimerRemainingSeconds: Long = 0,
    val comfortReady: Boolean = false,
    val comfortShown: Boolean = false,
    val isLoading: Boolean = true
) {
    val isTracking: Boolean get() = startDate != null
    val hasRecordedToday: Boolean get() = lastRecordDate == LocalDate.now()
    val isShakyTimerActive: Boolean get() = shakyTimerRemainingSeconds > 0
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = AppDataStore(application)
    private val repository = SoberRepository(dataStore)

    private val _shakyTimerRemaining = MutableStateFlow(0L)
    private var shakyTimerJob: Job? = null
    private var midnightResetJob: Job? = null

    val uiState: StateFlow<MainUiState> = combine(
        repository.fullState,
        _shakyTimerRemaining
    ) { state, timerRemaining ->
        MainUiState(
            startDate = state.startDate,
            currentStreak = state.currentStreak,
            dailyStatus = state.todayState.status,
            shakyCountToday = state.todayState.shakyCount,
            shakyTimestamps = state.todayState.shakyTimestamps,
            lastRecordDate = state.lastRecordDate,
            shakyTimerRemainingSeconds = timerRemaining,
            comfortReady = state.todayState.comfortReady,
            comfortShown = state.todayState.comfortShown,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainUiState()
    )

    private val _lastCheckedDate = MutableStateFlow(LocalDate.now())
    val lastCheckedDate: StateFlow<LocalDate> = _lastCheckedDate.asStateFlow()

    init {
        viewModelScope.launch {
            initializeIfFirstLaunch()
            val didReset = repository.checkAndResetIfNewDay()
            if (didReset) {
                clearShakyTimer()
            } else {
                restoreShakyTimerIfNeeded()
            }
            _lastCheckedDate.value = LocalDate.now()
        }
        scheduleMidnightReset()
    }

    fun checkDateChange() {
        viewModelScope.launch {
            val today = LocalDate.now()
            if (_lastCheckedDate.value.isBefore(today)) {
                val didReset = repository.checkAndResetIfNewDay()
                if (didReset) {
                    clearShakyTimer()
                }
                _lastCheckedDate.value = today
            }
        }
    }

    private suspend fun initializeIfFirstLaunch() {
        val startDate = dataStore.startDate.first()
        if (startDate == null) {
            repository.startTracking(LocalDate.now())
        }
    }

    // ========== Button Actions ==========

    fun onTodaySuccess() {
        viewModelScope.launch {
            if (hasRecordedToday()) return@launch
            repository.recordSuccess()
            clearShakyTimer()
        }
    }

    fun onDrink() {
        viewModelScope.launch {
            if (hasRecordedToday()) return@launch
            repository.recordFail()
            clearShakyTimer()
        }
    }

    fun onShaky() {
        viewModelScope.launch {
            repository.recordShaky()
            startShakyTimer()
            ComfortMessageWorker.schedule(getApplication())
        }
    }

    fun onComfortMessageSeen() {
        viewModelScope.launch {
            repository.markComfortMessageShown()
        }
    }

    // ========== Shaky Timer ==========

    private fun startShakyTimer() {
        shakyTimerJob?.cancel()
        val threeHoursInSeconds = 3 * 60 * 60L
        _shakyTimerRemaining.value = threeHoursInSeconds

        shakyTimerJob = viewModelScope.launch {
            while (_shakyTimerRemaining.value > 0) {
                delay(1_000)
                _shakyTimerRemaining.value -= 1
            }
        }
    }

    private suspend fun restoreShakyTimerIfNeeded() {
        val timestamps = dataStore.shakyTimestamps.first()
        if (timestamps.isEmpty()) return

        val lastShaky = timestamps.last()
        val now = LocalDateTime.now()
        val elapsed = Duration.between(lastShaky, now)
        val threeHours = Duration.ofHours(3)

        if (elapsed < threeHours) {
            val remaining = threeHours.minus(elapsed).seconds
            _shakyTimerRemaining.value = remaining

            shakyTimerJob?.cancel()
            shakyTimerJob = viewModelScope.launch {
                while (_shakyTimerRemaining.value > 0) {
                    delay(1_000)
                    _shakyTimerRemaining.value -= 1
                }
            }
        }
    }

    private fun clearShakyTimer() {
        shakyTimerJob?.cancel()
        _shakyTimerRemaining.value = 0
        ComfortMessageWorker.cancel(getApplication())
    }

    // ========== Midnight Reset ==========

    private fun scheduleMidnightReset() {
        midnightResetJob?.cancel()
        midnightResetJob = viewModelScope.launch {
            while (true) {
                val now = LocalDateTime.now()
                val nextMidnight = LocalDateTime.of(
                    now.toLocalDate().plusDays(1),
                    LocalTime.MIDNIGHT
                )
                val delayMs = Duration.between(now, nextMidnight).toMillis()
                delay(delayMs)

                checkDateChange()
            }
        }
    }

    // ========== Helpers ==========

    private suspend fun hasRecordedToday(): Boolean {
        val lastDate = dataStore.lastRecordDate.first()
        return lastDate == LocalDate.now()
    }
}
