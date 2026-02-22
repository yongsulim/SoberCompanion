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

/**
 * 홈 화면의 UI 상태를 나타내는 불변 데이터 클래스.
 *
 * Compose UI는 이 클래스만 구독하며, 모든 UI 로직은 이 상태에서 파생됩니다.
 * sealed class 대신 단일 data class를 사용해 부분 업데이트가 쉽도록 설계했습니다.
 */
data class MainUiState(
    /** 금주 시작 날짜. null이면 아직 추적 시작 전 */
    val startDate: LocalDate? = null,

    /** 현재 연속 금주 일수 */
    val currentStreak: Int = 0,

    /** 오늘의 금주 상태 (SUCCESS / SHAKY / FAIL) */
    val dailyStatus: RecordStatus = RecordStatus.SUCCESS,

    /** 오늘 흔들림 버튼을 누른 횟수 */
    val shakyCountToday: Int = 0,

    /** 오늘 흔들림이 발생한 시각 목록 */
    val shakyTimestamps: List<LocalDateTime> = emptyList(),

    /** 마지막으로 성공/실패를 기록한 날짜 */
    val lastRecordDate: LocalDate? = null,

    /** 흔들림 타이머 남은 시간(초). 0이면 타이머 비활성 */
    val shakyTimerRemainingSeconds: Long = 0,

    /** 위로 메시지를 표시할 준비가 됐는지 여부 */
    val comfortReady: Boolean = false,

    /** 이번 흔들림 이벤트에서 위로 메시지를 이미 확인했는지 여부 */
    val comfortShown: Boolean = false,

    /** 초기 데이터 로딩 중 여부. true이면 로딩 인디케이터를 표시 */
    val isLoading: Boolean = true
) {
    /** 금주 추적이 시작된 상태인지 여부 */
    val isTracking: Boolean get() = startDate != null

    /** 오늘 이미 상태를 기록했는지 여부 (성공/실패 중복 방지에 사용) */
    val hasRecordedToday: Boolean get() = lastRecordDate == LocalDate.now()

    /** 흔들림 타이머가 현재 카운트다운 중인지 여부 */
    val isShakyTimerActive: Boolean get() = shakyTimerRemainingSeconds > 0
}

/**
 * 홈 화면의 모든 UI 로직을 담당하는 ViewModel.
 *
 * 주요 책임:
 * 1. SoberRepository의 Flow를 UI State로 변환
 * 2. 흔들림 타이머 관리 (3시간 카운트다운 + 앱 재시작 시 복원)
 * 3. 자정 리셋 스케줄링 (매일 자정에 데이터 초기화 실행)
 * 4. 사용자 액션 처리 (성공/흔들림/음주 기록)
 *
 * AndroidViewModel을 상속해 Application Context에 접근합니다.
 * WorkManager 스케줄링에 Context가 필요하기 때문입니다.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = AppDataStore(application)
    private val repository = SoberRepository(dataStore)

    /** 흔들림 타이머 남은 시간(초). Repository와 별도로 관리하는 UI 전용 상태 */
    private val _shakyTimerRemaining = MutableStateFlow(0L)

    /** 흔들림 타이머 코루틴 Job. cancel()로 타이머를 중단합니다 */
    private var shakyTimerJob: Job? = null

    /** 자정 리셋 코루틴 Job. ViewModel 생명주기 동안 계속 실행됩니다 */
    private var midnightResetJob: Job? = null

    /**
     * 홈 화면이 구독하는 메인 UI State.
     *
     * repository.fullState와 _shakyTimerRemaining을 합쳐서 하나의 상태로 만듭니다.
     * WhileSubscribed(5_000): 마지막 구독자가 사라진 후 5초 동안 Flow를 유지합니다.
     * (화면 회전 등 일시적 구독 해제 시 불필요한 재계산 방지)
     */
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

    /** 마지막으로 날짜 변경을 확인한 날짜. UI 폴링에서 중복 처리 방지에 사용 */
    private val _lastCheckedDate = MutableStateFlow(LocalDate.now())
    val lastCheckedDate: StateFlow<LocalDate> = _lastCheckedDate.asStateFlow()

    init {
        viewModelScope.launch {
            // 최초 실행이면 오늘 날짜로 금주 추적 시작
            initializeIfFirstLaunch()

            // 자정을 넘겼다면 리셋, 아니라면 타이머 복원
            val didReset = repository.checkAndResetIfNewDay()
            if (didReset) {
                clearShakyTimer()
            } else {
                // 앱이 꺼진 동안 경과한 시간을 계산해 타이머 재시작
                restoreShakyTimerIfNeeded()
            }
            _lastCheckedDate.value = LocalDate.now()
        }
        // 자정마다 자동 리셋을 위한 코루틴 시작
        scheduleMidnightReset()
    }

    /**
     * 날짜 변경 여부를 확인하고 필요하면 리셋합니다.
     * HomeScreen에서 30초마다 LaunchedEffect로 호출됩니다.
     * (앱이 포그라운드에 있을 때 자정 리셋을 보장하기 위한 폴링)
     */
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

    /**
     * DataStore에 startDate가 없으면 오늘부터 추적을 시작합니다.
     * 온보딩을 건너뛴 경우나 데이터가 초기화된 경우를 대비합니다.
     */
    private suspend fun initializeIfFirstLaunch() {
        val startDate = dataStore.startDate.first()
        if (startDate == null) {
            repository.startTracking(LocalDate.now())
        }
    }

    // ========== Button Actions ==========

    /**
     * 홈 화면 "오늘도 성공" 버튼 핸들러.
     * 오늘 이미 기록했으면 중복 기록을 방지합니다.
     */
    fun onTodaySuccess() {
        viewModelScope.launch {
            if (hasRecordedToday()) return@launch
            repository.recordSuccess()
            clearShakyTimer()
        }
    }

    /**
     * 홈 화면 "음주했어요" 버튼 핸들러.
     * 오늘 이미 기록했으면 중복 기록을 방지합니다.
     * 스트릭이 오늘 날짜로 리셋됩니다.
     */
    fun onDrink() {
        viewModelScope.launch {
            if (hasRecordedToday()) return@launch
            repository.recordFail()
            clearShakyTimer()
        }
    }

    /**
     * 홈 화면 "흔들려요" 버튼 핸들러.
     * 하루에 여러 번 누를 수 있습니다.
     * 3시간 타이머를 시작하고, 3시간 후 위로 메시지가 표시됩니다.
     */
    fun onShaky() {
        viewModelScope.launch {
            repository.recordShaky()
            startShakyTimer()
            // WorkManager로 3시간 후 comfort_ready_flag를 true로 설정
            ComfortMessageWorker.schedule(getApplication())
        }
    }

    /**
     * 위로 메시지 카드를 닫을 때 호출됩니다.
     * 같은 흔들림 이벤트에서 메시지가 다시 뜨지 않도록 처리합니다.
     */
    fun onComfortMessageSeen() {
        viewModelScope.launch {
            repository.markComfortMessageShown()
        }
    }

    // ========== Shaky Timer ==========

    /**
     * 3시간(10,800초) 카운트다운 타이머를 시작합니다.
     * 이미 실행 중인 타이머가 있으면 취소하고 새로 시작합니다.
     * (흔들림 버튼을 여러 번 누르면 타이머가 매번 3시간으로 리셋됨)
     */
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

    /**
     * 앱 재시작 시 이전에 실행 중이던 흔들림 타이머를 복원합니다.
     *
     * 마지막 흔들림 타임스탬프와 현재 시간의 차이를 계산해
     * 3시간이 아직 지나지 않았으면 남은 시간으로 타이머를 재시작합니다.
     */
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

    /**
     * 흔들림 타이머를 중단하고 0으로 초기화합니다.
     * 성공/음주 기록 또는 자정 리셋 시 호출됩니다.
     */
    private fun clearShakyTimer() {
        shakyTimerJob?.cancel()
        _shakyTimerRemaining.value = 0
        // 예약된 WorkManager 작업도 함께 취소
        ComfortMessageWorker.cancel(getApplication())
    }

    // ========== Midnight Reset ==========

    /**
     * 매일 자정에 checkDateChange()를 자동으로 호출하는 코루틴을 시작합니다.
     *
     * 동작 원리:
     * 1. 현재 시각에서 다음 자정까지의 시간을 계산
     * 2. 그만큼 delay 후 checkDateChange() 호출
     * 3. while(true)로 반복하여 매일 자정마다 실행
     *
     * ViewModel이 소멸되면 viewModelScope와 함께 자동으로 취소됩니다.
     */
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

    /**
     * 오늘 이미 상태를 기록했는지 확인합니다.
     * 성공/음주 중복 기록 방지에 사용됩니다.
     * (흔들림은 하루에 여러 번 가능하므로 이 확인을 거치지 않습니다)
     */
    private suspend fun hasRecordedToday(): Boolean {
        val lastDate = dataStore.lastRecordDate.first()
        return lastDate == LocalDate.now()
    }
}
