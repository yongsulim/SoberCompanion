package com.sobercompanion.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * 금주 추적 비즈니스 로직을 담당하는 Repository.
 *
 * AppDataStore를 직접 노출하지 않고 의미 있는 Flow와 액션으로 래핑합니다.
 * ViewModel은 이 클래스만 참조하며 DataStore 구조를 몰라도 됩니다.
 *
 * @param dataStore DataStore 래퍼 인스턴스
 */
class SoberRepository(private val dataStore: AppDataStore) {

    // ========== Individual Flows ==========
    // DataStore Flow를 그대로 노출합니다.

    val startDate: Flow<LocalDate?> = dataStore.startDate
    val currentStreak: Flow<Int> = dataStore.currentStreak
    val lastRecordDate: Flow<LocalDate?> = dataStore.lastRecordDate
    val dailyStatus: Flow<RecordStatus> = dataStore.dailyStatus
    val shakyCountToday: Flow<Int> = dataStore.shakyCountToday
    val shakyTimestamps: Flow<List<LocalDateTime>> = dataStore.shakyTimestamps
    val comfortReadyFlag: Flow<Boolean> = dataStore.comfortReadyFlag
    val comfortMessageShown: Flow<Boolean> = dataStore.comfortMessageShown

    // ========== Derived Flows ==========
    // 저장된 값에서 파생된 계산 결과를 Flow로 제공합니다.

    /** 금주 추적이 시작됐는지 여부 (startDate가 설정되어 있으면 true) */
    val isTracking: Flow<Boolean> = startDate.map { it != null }

    /**
     * startDate로부터 오늘까지의 실제 경과 일수.
     * currentStreak(저장값)과 다를 수 있으니 주의하세요.
     * currentStreak는 자정 리셋 시 업데이트되므로, 앱이 오랫동안 실행되지 않았다면
     * 이 값이 더 정확합니다.
     */
    val actualStreakDays: Flow<Int> = startDate.map { start ->
        start?.let {
            ChronoUnit.DAYS.between(it, LocalDate.now()).toInt()
        } ?: 0
    }

    /**
     * 오늘 기록이 필요한지 여부.
     * lastRecordDate가 null이거나 오늘 이전 날짜면 true를 emit합니다.
     */
    val needsDailyReset: Flow<Boolean> = lastRecordDate.map { lastDate ->
        lastDate == null || lastDate.isBefore(LocalDate.now())
    }

    /**
     * 오늘 하루의 상태를 하나의 Flow로 묶어서 제공합니다.
     * 홈 화면에서 여러 값을 개별 구독하는 대신 이 Flow 하나만 구독하면 됩니다.
     */
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

    /**
     * 앱 전체 상태를 하나의 Flow로 묶어서 제공합니다.
     * MainViewModel의 uiState를 구성하는 데 사용됩니다.
     */
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

    /**
     * 금주 추적을 시작합니다.
     * 온보딩 완료 시 또는 스트릭 재시작 시 호출됩니다.
     *
     * @param date 금주 시작 날짜 (기본값: 오늘)
     */
    suspend fun startTracking(date: LocalDate = LocalDate.now()) {
        dataStore.setStartDate(date)
        dataStore.setCurrentStreak(0)
        dataStore.resetDailyData()
    }

    /** 모든 데이터를 초기화하고 추적을 중단합니다 */
    suspend fun resetTracking() {
        dataStore.resetAllData()
    }

    /** 오늘부터 새로 금주를 시작합니다 (음주 후 재시작과 동일) */
    suspend fun restartFromToday() {
        startTracking(LocalDate.now())
    }

    /**
     * 오늘 금주 성공을 기록합니다.
     * 중복 기록 방지는 ViewModel에서 처리합니다 (hasRecordedToday 확인).
     */
    suspend fun recordSuccess() {
        checkAndResetIfNewDay()
        dataStore.setDailyStatus(RecordStatus.SUCCESS)
        dataStore.setLastRecordDate(LocalDate.now())
        updateStreak()
    }

    /**
     * 흔들림(음주 충동)을 기록합니다.
     * 성공/실패와 달리 하루에 여러 번 기록할 수 있습니다.
     * comfortReadyFlag를 true로 설정해 위로 메시지 준비 상태를 알립니다.
     */
    suspend fun recordShaky() {
        checkAndResetIfNewDay()
        dataStore.setDailyStatus(RecordStatus.SHAKY)
        dataStore.addShakyTimestamp(LocalDateTime.now())
        dataStore.setComfortReadyFlag(true)
    }

    /**
     * 음주(실패)를 기록합니다.
     * startDate를 오늘로 리셋해 스트릭을 0부터 다시 시작합니다.
     */
    suspend fun recordFail() {
        checkAndResetIfNewDay()
        dataStore.setDailyStatus(RecordStatus.FAIL)
        dataStore.setLastRecordDate(LocalDate.now())
        // 음주 시 streak 리셋: 오늘부터 새로 카운트
        dataStore.setStartDate(LocalDate.now())
        dataStore.setCurrentStreak(0)
    }

    /**
     * 위로 메시지를 확인했음을 기록합니다.
     * comfortShown = true, comfortReadyFlag = false로 설정해 중복 표시를 막습니다.
     */
    suspend fun markComfortMessageShown() {
        dataStore.setComfortMessageShown(true)
        dataStore.setComfortReadyFlag(false)
    }

    /**
     * 날짜가 바뀌었으면 흔들림 데이터를 리셋하고 스트릭을 갱신합니다.
     *
     * 판단 기준: lastRecordDate와 마지막 shakyTimestamp 중 더 최근 날짜가 오늘 이전이면 리셋.
     * 앱이 오랫동안 실행되지 않아도 올바르게 동작하도록 두 값을 모두 확인합니다.
     *
     * @return 리셋이 실행됐으면 true, 이미 오늘이면 false
     */
    suspend fun checkAndResetIfNewDay(): Boolean {
        val lastDate = dataStore.lastRecordDate.first()
        val timestamps = dataStore.shakyTimestamps.first()
        val today = LocalDate.now()

        // 마지막 활동 날짜 = lastRecordDate와 shakyTimestamp 중 더 최근 날짜
        val lastActivityDate = listOfNotNull(
            lastDate,
            timestamps.lastOrNull()?.toLocalDate()
        ).maxOrNull()

        if (lastActivityDate != null && lastActivityDate.isBefore(today)) {
            dataStore.resetShakyAndComfortData()
            updateStreak()
            return true
        }
        return false
    }

    /**
     * startDate로부터 오늘까지의 일수를 계산해 currentStreak를 업데이트합니다.
     * startDate가 없으면 아무 작업도 하지 않습니다.
     */
    private suspend fun updateStreak() {
        val start = dataStore.startDate.first() ?: return
        val streak = ChronoUnit.DAYS.between(start, LocalDate.now()).toInt()
        dataStore.setCurrentStreak(streak)
    }

    // ========== Query Methods ==========
    // Flow 대신 일회성 값이 필요할 때 사용합니다.

    suspend fun getStartDate(): LocalDate? = dataStore.startDate.first()
    suspend fun getCurrentStreak(): Int = dataStore.currentStreak.first()
    suspend fun getDailyStatus(): RecordStatus = dataStore.dailyStatus.first()
    suspend fun getShakyCountToday(): Int = dataStore.shakyCountToday.first()
    suspend fun isComfortReady(): Boolean = dataStore.comfortReadyFlag.first()
}

// ========== Data Classes ==========

/**
 * 오늘 하루의 상태를 하나로 묶은 데이터 클래스.
 * SoberRepository.todayState Flow를 통해 emit됩니다.
 */
data class TodayState(
    val status: RecordStatus = RecordStatus.SUCCESS,
    val shakyCount: Int = 0,                              // 오늘 흔들림 횟수
    val shakyTimestamps: List<LocalDateTime> = emptyList(), // 흔들림 발생 시각 목록
    val comfortReady: Boolean = false,                    // 위로 메시지 표시 준비 완료
    val comfortShown: Boolean = false                     // 위로 메시지 이미 확인함
)

/**
 * 앱 전체 상태를 하나로 묶은 데이터 클래스.
 * MainViewModel.uiState를 구성하는 소스로 사용됩니다.
 */
data class SoberState(
    val startDate: LocalDate? = null,
    val currentStreak: Int = 0,
    val lastRecordDate: LocalDate? = null,
    val todayState: TodayState = TodayState()
) {
    /** 금주 추적이 시작된 상태인지 여부 */
    val isTracking: Boolean get() = startDate != null

    /**
     * startDate로부터 계산한 실제 경과 일수.
     * currentStreak와 달리 자정 업데이트 없이도 항상 정확한 값을 반환합니다.
     */
    val actualDays: Int get() = startDate?.let {
        ChronoUnit.DAYS.between(it, LocalDate.now()).toInt()
    } ?: 0
}
