package com.sobercompanion.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Context 확장 프로퍼티로 DataStore 인스턴스를 생성합니다.
 * 앱 전역에서 동일한 "sober_data" 파일을 공유합니다.
 */
val Context.soberDataStore: DataStore<Preferences> by preferencesDataStore(name = "sober_data")

/**
 * 금주 앱의 실시간 세션 데이터를 저장하는 DataStore 래퍼 클래스.
 *
 * Room DB와의 역할 분리:
 * - AppDataStore: 빠른 읽기/쓰기가 필요한 현재 상태 (오늘 기록, 스트릭, 타이머 등)
 * - Room DB: 장기 이력 보관 및 복잡한 쿼리 (통계, 마일스톤 등)
 *
 * 모든 Flow는 데이터 변경 시 자동으로 UI에 반영됩니다.
 * 타임스탬프 목록은 "|" 구분자로 직렬화하여 단일 문자열로 저장합니다.
 */
class AppDataStore(private val context: Context) {

    /**
     * DataStore 키 상수 모음.
     * 키 이름 변경 시 기존 사용자의 데이터가 손실되므로 신중하게 수정해야 합니다.
     */
    private object Keys {
        val START_DATE = stringPreferencesKey("start_date")           // 금주 시작 날짜 (ISO-8601)
        val CURRENT_STREAK = intPreferencesKey("current_streak")      // 연속 금주 일수
        val LAST_RECORD_DATE = stringPreferencesKey("last_record_date") // 마지막 기록 날짜
        val DAILY_STATUS = stringPreferencesKey("daily_status")       // 오늘 상태 (SUCCESS/SHAKY/FAIL)
        val SHAKY_COUNT_TODAY = intPreferencesKey("shaky_count_today") // 오늘 흔들림 횟수
        val SHAKY_TIMESTAMPS = stringPreferencesKey("shaky_timestamps") // 흔들림 발생 시각 목록 ("|" 구분)
        val COMFORT_READY_FLAG = booleanPreferencesKey("comfort_ready_flag") // 위로 메시지 표시 준비 플래그
        val COMFORT_MESSAGE_SHOWN = booleanPreferencesKey("comfort_message_shown") // 위로 메시지 확인 여부
    }

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // ========== Read Operations (Flow) ==========
    // 각 Flow는 DataStore 값이 바뀌면 자동으로 새 값을 emit합니다.

    /** 금주 시작 날짜. 온보딩 미완료 또는 아직 설정 전이면 null */
    val startDate: Flow<LocalDate?> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.START_DATE]?.let { LocalDate.parse(it, dateFormatter) }
    }

    /** 현재 연속 금주 일수. 자정마다 또는 음주 기록 시 업데이트됨 */
    val currentStreak: Flow<Int> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.CURRENT_STREAK] ?: 0
    }

    /** 마지막으로 상태를 기록한 날짜. 자정 리셋 여부 판단에 사용됨 */
    val lastRecordDate: Flow<LocalDate?> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.LAST_RECORD_DATE]?.let { LocalDate.parse(it, dateFormatter) }
    }

    /** 오늘의 금주 상태. 알 수 없는 값이면 SUCCESS로 기본 처리됨 */
    val dailyStatus: Flow<RecordStatus> = context.soberDataStore.data.map { prefs ->
        RecordStatus.fromString(prefs[Keys.DAILY_STATUS])
    }

    /** 오늘 흔들림 버튼을 누른 횟수. 자정에 0으로 초기화됨 */
    val shakyCountToday: Flow<Int> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.SHAKY_COUNT_TODAY] ?: 0
    }

    /**
     * 오늘 흔들림이 발생한 시각 목록.
     * 저장 형식: "2024-01-15T10:30:00|2024-01-15T14:20:00"
     * 가장 최근 타임스탬프를 기준으로 3시간 타이머를 복원하는 데 사용됨
     */
    val shakyTimestamps: Flow<List<LocalDateTime>> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.SHAKY_TIMESTAMPS]
            ?.takeIf { it.isNotEmpty() }
            ?.split(TIMESTAMP_DELIMITER)
            ?.map { LocalDateTime.parse(it, dateTimeFormatter) }
            ?: emptyList()
    }

    /**
     * 위로 메시지 표시 준비 플래그.
     * ComfortMessageWorker가 3시간 후 true로 설정하면 UI에 메시지 카드가 나타남.
     * 사용자가 메시지를 확인하면 false로 리셋됨
     */
    val comfortReadyFlag: Flow<Boolean> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.COMFORT_READY_FLAG] ?: false
    }

    /**
     * 위로 메시지 확인 여부.
     * true이면 같은 흔들림 이벤트에서 메시지가 중복 표시되지 않음.
     * 자정 리셋 시 false로 초기화됨
     */
    val comfortMessageShown: Flow<Boolean> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.COMFORT_MESSAGE_SHOWN] ?: false
    }

    // ========== Write Operations ==========

    /** 금주 시작 날짜를 설정합니다. 온보딩 완료 또는 스트릭 리셋 시 호출됨 */
    suspend fun setStartDate(date: LocalDate) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.START_DATE] = date.format(dateFormatter)
        }
    }

    /** 연속 금주 일수를 업데이트합니다. startDate로부터 계산된 값을 저장 */
    suspend fun setCurrentStreak(streak: Int) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.CURRENT_STREAK] = streak
        }
    }

    /** 마지막 기록 날짜를 오늘로 업데이트합니다. 성공/실패 기록 시 호출됨 */
    suspend fun setLastRecordDate(date: LocalDate) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.LAST_RECORD_DATE] = date.format(dateFormatter)
        }
    }

    /** 오늘의 금주 상태를 저장합니다 */
    suspend fun setDailyStatus(status: RecordStatus) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.DAILY_STATUS] = status.name
        }
    }

    /** 흔들림 횟수를 직접 지정합니다 (주로 리셋 시 0으로 설정) */
    suspend fun setShakyCountToday(count: Int) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.SHAKY_COUNT_TODAY] = count
        }
    }

    /** 흔들림 타임스탬프 목록 전체를 교체합니다 */
    suspend fun setShakyTimestamps(timestamps: List<LocalDateTime>) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.SHAKY_TIMESTAMPS] = timestamps
                .joinToString(TIMESTAMP_DELIMITER) { it.format(dateTimeFormatter) }
        }
    }

    /**
     * 새 흔들림 타임스탬프를 목록에 추가합니다.
     * 기존 목록을 읽어서 새 항목을 덧붙이고, 카운트도 함께 업데이트합니다.
     * DataStore의 트랜잭션 내에서 읽기/쓰기를 원자적으로 처리합니다.
     */
    suspend fun addShakyTimestamp(timestamp: LocalDateTime) {
        context.soberDataStore.edit { prefs ->
            val current = prefs[Keys.SHAKY_TIMESTAMPS]
                ?.takeIf { it.isNotEmpty() }
                ?.split(TIMESTAMP_DELIMITER)
                ?.toMutableList()
                ?: mutableListOf()

            current.add(timestamp.format(dateTimeFormatter))
            prefs[Keys.SHAKY_TIMESTAMPS] = current.joinToString(TIMESTAMP_DELIMITER)
            prefs[Keys.SHAKY_COUNT_TODAY] = current.size
        }
    }

    /** 위로 메시지 표시 준비 플래그를 설정합니다 */
    suspend fun setComfortReadyFlag(ready: Boolean) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.COMFORT_READY_FLAG] = ready
        }
    }

    /** 위로 메시지를 확인했음을 저장합니다 */
    suspend fun setComfortMessageShown(shown: Boolean) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.COMFORT_MESSAGE_SHOWN] = shown
        }
    }

    // ========== Batch Operations ==========

    /**
     * 오늘의 일일 데이터를 초기 상태로 리셋합니다.
     * 자정이 지나거나 온보딩 완료 시 호출됩니다.
     * startDate와 currentStreak는 유지됩니다.
     */
    suspend fun resetDailyData() {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.DAILY_STATUS] = RecordStatus.SUCCESS.name
            prefs[Keys.SHAKY_COUNT_TODAY] = 0
            prefs[Keys.SHAKY_TIMESTAMPS] = ""
            prefs[Keys.COMFORT_READY_FLAG] = false
            prefs[Keys.COMFORT_MESSAGE_SHOWN] = false
        }
    }

    /**
     * 흔들림 관련 데이터만 리셋합니다.
     * 자정이 지났을 때 새날을 시작하면서 호출됩니다.
     * 금주 상태(SUCCESS/FAIL)는 유지됩니다.
     */
    suspend fun resetShakyAndComfortData() {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.SHAKY_COUNT_TODAY] = 0
            prefs[Keys.SHAKY_TIMESTAMPS] = ""
            prefs[Keys.COMFORT_READY_FLAG] = false
            prefs[Keys.COMFORT_MESSAGE_SHOWN] = false
        }
    }

    /**
     * 모든 데이터를 초기화합니다.
     * 앱 완전 리셋 또는 개발/테스트 목적으로만 사용하세요.
     */
    suspend fun resetAllData() {
        context.soberDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    companion object {
        /** 타임스탬프 목록 직렬화에 사용하는 구분자. 날짜 포맷에 포함되지 않는 문자여야 함 */
        private const val TIMESTAMP_DELIMITER = "|"
    }
}
