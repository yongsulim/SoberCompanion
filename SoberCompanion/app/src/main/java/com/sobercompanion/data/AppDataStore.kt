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

val Context.soberDataStore: DataStore<Preferences> by preferencesDataStore(name = "sober_data")

class AppDataStore(private val context: Context) {

    private object Keys {
        val START_DATE = stringPreferencesKey("start_date")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val LAST_RECORD_DATE = stringPreferencesKey("last_record_date")
        val DAILY_STATUS = stringPreferencesKey("daily_status")
        val SHAKY_COUNT_TODAY = intPreferencesKey("shaky_count_today")
        val SHAKY_TIMESTAMPS = stringPreferencesKey("shaky_timestamps")
        val COMFORT_READY_FLAG = booleanPreferencesKey("comfort_ready_flag")
        val COMFORT_MESSAGE_SHOWN = booleanPreferencesKey("comfort_message_shown")
    }

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // ========== Read Operations (Flow) ==========

    val startDate: Flow<LocalDate?> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.START_DATE]?.let { LocalDate.parse(it, dateFormatter) }
    }

    val currentStreak: Flow<Int> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.CURRENT_STREAK] ?: 0
    }

    val lastRecordDate: Flow<LocalDate?> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.LAST_RECORD_DATE]?.let { LocalDate.parse(it, dateFormatter) }
    }

    val dailyStatus: Flow<RecordStatus> = context.soberDataStore.data.map { prefs ->
        RecordStatus.fromString(prefs[Keys.DAILY_STATUS])
    }

    val shakyCountToday: Flow<Int> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.SHAKY_COUNT_TODAY] ?: 0
    }

    val shakyTimestamps: Flow<List<LocalDateTime>> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.SHAKY_TIMESTAMPS]
            ?.takeIf { it.isNotEmpty() }
            ?.split(TIMESTAMP_DELIMITER)
            ?.map { LocalDateTime.parse(it, dateTimeFormatter) }
            ?: emptyList()
    }

    val comfortReadyFlag: Flow<Boolean> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.COMFORT_READY_FLAG] ?: false
    }

    val comfortMessageShown: Flow<Boolean> = context.soberDataStore.data.map { prefs ->
        prefs[Keys.COMFORT_MESSAGE_SHOWN] ?: false
    }

    // ========== Write Operations ==========

    suspend fun setStartDate(date: LocalDate) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.START_DATE] = date.format(dateFormatter)
        }
    }

    suspend fun setCurrentStreak(streak: Int) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.CURRENT_STREAK] = streak
        }
    }

    suspend fun setLastRecordDate(date: LocalDate) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.LAST_RECORD_DATE] = date.format(dateFormatter)
        }
    }

    suspend fun setDailyStatus(status: RecordStatus) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.DAILY_STATUS] = status.name
        }
    }

    suspend fun setShakyCountToday(count: Int) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.SHAKY_COUNT_TODAY] = count
        }
    }

    suspend fun setShakyTimestamps(timestamps: List<LocalDateTime>) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.SHAKY_TIMESTAMPS] = timestamps
                .joinToString(TIMESTAMP_DELIMITER) { it.format(dateTimeFormatter) }
        }
    }

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

    suspend fun setComfortReadyFlag(ready: Boolean) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.COMFORT_READY_FLAG] = ready
        }
    }

    suspend fun setComfortMessageShown(shown: Boolean) {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.COMFORT_MESSAGE_SHOWN] = shown
        }
    }

    // ========== Batch Operations ==========

    suspend fun resetDailyData() {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.DAILY_STATUS] = RecordStatus.SUCCESS.name
            prefs[Keys.SHAKY_COUNT_TODAY] = 0
            prefs[Keys.SHAKY_TIMESTAMPS] = ""
            prefs[Keys.COMFORT_READY_FLAG] = false
            prefs[Keys.COMFORT_MESSAGE_SHOWN] = false
        }
    }

    suspend fun resetShakyAndComfortData() {
        context.soberDataStore.edit { prefs ->
            prefs[Keys.SHAKY_COUNT_TODAY] = 0
            prefs[Keys.SHAKY_TIMESTAMPS] = ""
            prefs[Keys.COMFORT_READY_FLAG] = false
            prefs[Keys.COMFORT_MESSAGE_SHOWN] = false
        }
    }

    suspend fun resetAllData() {
        context.soberDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    companion object {
        private const val TIMESTAMP_DELIMITER = "|"
    }
}
