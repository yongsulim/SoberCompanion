package com.sobercompanion.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class SoberRepository(private val dataStore: AppDataStore) {

    // ========== Individual Flows ==========

    val startDate: Flow<LocalDate?> = dataStore.startDate

    val currentStreak: Flow<Int> = dataStore.currentStreak

    val lastRecordDate: Flow<LocalDate?> = dataStore.lastRecordDate

    val dailyStatus: Flow<RecordStatus> = dataStore.dailyStatus

    val shakyCountToday: Flow<Int> = dataStore.shakyCountToday

    val shakyTimestamps: Flow<List<LocalDateTime>> = dataStore.shakyTimestamps

    val comfortReadyFlag: Flow<Boolean> = dataStore.comfortReadyFlag

    val comfortMessageShown: Flow<Boolean> = dataStore.comfortMessageShown

    // ========== Derived Flows ==========

    val isTracking: Flow<Boolean> = startDate.map { it != null }

    val actualStreakDays: Flow<Int> = startDate.map { start ->
        start?.let {
            ChronoUnit.DAYS.between(it, LocalDate.now()).toInt()
        } ?: 0
    }

    val needsDailyReset: Flow<Boolean> = lastRecordDate.map { lastDate ->
        lastDate == null || lastDate.isBefore(LocalDate.now())
    }

    val todayState: Flow<TodayState> = combine(
        dailyStatus,
        shakyCountToday,
        shakyTimestamps,
        comfortReadyFlag,
        comfortMessageShown
    ) { status, shakyCount, timestamps, comfortReady, comfortShown ->
        TodayState(
            status = status,
            shakyCount = shakyCount,
            shakyTimestamps = timestamps,
            comfortReady = comfortReady,
            comfortShown = comfortShown
        )
    }

    val fullState: Flow<SoberState> = combine(
        startDate,
        currentStreak,
        lastRecordDate,
        todayState
    ) { start, streak, lastRecord, today ->
        SoberState(
            startDate = start,
            currentStreak = streak,
            lastRecordDate = lastRecord,
            todayState = today
        )
    }

    // ========== Actions ==========

    suspend fun startTracking(date: LocalDate = LocalDate.now()) {
        dataStore.setStartDate(date)
        dataStore.setCurrentStreak(0)
        dataStore.setLastRecordDate(date)
        dataStore.resetDailyData()
    }

    suspend fun resetTracking() {
        dataStore.resetAllData()
    }

    suspend fun restartFromToday() {
        startTracking(LocalDate.now())
    }

    suspend fun recordSuccess() {
        checkAndResetIfNewDay()
        dataStore.setDailyStatus(RecordStatus.SUCCESS)
        dataStore.setLastRecordDate(LocalDate.now())
        updateStreak()
    }

    suspend fun recordShaky() {
        checkAndResetIfNewDay()
        dataStore.setDailyStatus(RecordStatus.SHAKY)
        dataStore.addShakyTimestamp(LocalDateTime.now())
        dataStore.setComfortReadyFlag(true)
        dataStore.setLastRecordDate(LocalDate.now())
    }

    suspend fun recordFail() {
        checkAndResetIfNewDay()
        dataStore.setDailyStatus(RecordStatus.FAIL)
        dataStore.setLastRecordDate(LocalDate.now())
        // 실패 시 streak 리셋하고 다시 시작
        dataStore.setStartDate(LocalDate.now())
        dataStore.setCurrentStreak(0)
    }

    suspend fun markComfortMessageShown() {
        dataStore.setComfortMessageShown(true)
        dataStore.setComfortReadyFlag(false)
    }

    suspend fun checkAndResetIfNewDay(): Boolean {
        val lastDate = dataStore.lastRecordDate.first()
        val today = LocalDate.now()

        if (lastDate != null && lastDate.isBefore(today)) {
            dataStore.resetShakyAndComfortData()
            updateStreak()
            return true
        }
        return false
    }

    private suspend fun updateStreak() {
        val start = dataStore.startDate.first() ?: return
        val streak = ChronoUnit.DAYS.between(start, LocalDate.now()).toInt()
        dataStore.setCurrentStreak(streak)
    }

    // ========== Query Methods ==========

    suspend fun getStartDate(): LocalDate? = dataStore.startDate.first()

    suspend fun getCurrentStreak(): Int = dataStore.currentStreak.first()

    suspend fun getDailyStatus(): RecordStatus = dataStore.dailyStatus.first()

    suspend fun getShakyCountToday(): Int = dataStore.shakyCountToday.first()

    suspend fun isComfortReady(): Boolean = dataStore.comfortReadyFlag.first()
}

// ========== Data Classes ==========

data class TodayState(
    val status: RecordStatus = RecordStatus.SUCCESS,
    val shakyCount: Int = 0,
    val shakyTimestamps: List<LocalDateTime> = emptyList(),
    val comfortReady: Boolean = false,
    val comfortShown: Boolean = false
)

data class SoberState(
    val startDate: LocalDate? = null,
    val currentStreak: Int = 0,
    val lastRecordDate: LocalDate? = null,
    val todayState: TodayState = TodayState()
) {
    val isTracking: Boolean get() = startDate != null

    val actualDays: Int get() = startDate?.let {
        ChronoUnit.DAYS.between(it, LocalDate.now()).toInt()
    } ?: 0
}
