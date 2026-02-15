package com.sobercompanion.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sobercompanion.data.local.entity.DailyLog
import com.sobercompanion.data.local.entity.Milestone
import com.sobercompanion.data.local.entity.MotivationalQuote
import com.sobercompanion.data.local.entity.SobrietyRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface SobrietyDao {
    // Sobriety Records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSobrietyRecord(record: SobrietyRecord): Long

    @Update
    suspend fun updateSobrietyRecord(record: SobrietyRecord)

    @Delete
    suspend fun deleteSobrietyRecord(record: SobrietyRecord)

    @Query("SELECT * FROM sobriety_records WHERE isActive = 1 LIMIT 1")
    fun getActiveSobrietyRecord(): Flow<SobrietyRecord?>

    @Query("SELECT * FROM sobriety_records ORDER BY startDate DESC")
    fun getAllSobrietyRecords(): Flow<List<SobrietyRecord>>

    @Query("UPDATE sobriety_records SET isActive = 0, endDate = :endDate WHERE isActive = 1")
    suspend fun endCurrentSobriety(endDate: java.time.LocalDateTime)

    // Daily Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLog(log: DailyLog): Long

    @Update
    suspend fun updateDailyLog(log: DailyLog)

    @Delete
    suspend fun deleteDailyLog(log: DailyLog)

    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    suspend fun getDailyLogByDate(date: LocalDate): DailyLog?

    @Query("SELECT * FROM daily_logs ORDER BY date DESC")
    fun getAllDailyLogs(): Flow<List<DailyLog>>

    @Query("SELECT * FROM daily_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getDailyLogsBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyLog>>

    @Query("SELECT * FROM daily_logs ORDER BY date DESC LIMIT :limit")
    fun getRecentDailyLogs(limit: Int): Flow<List<DailyLog>>

    // Milestones
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: Milestone): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<Milestone>)

    @Update
    suspend fun updateMilestone(milestone: Milestone)

    @Query("SELECT * FROM milestones ORDER BY targetDays ASC")
    fun getAllMilestones(): Flow<List<Milestone>>

    @Query("SELECT * FROM milestones WHERE isAchieved = 0 ORDER BY targetDays ASC")
    fun getUnachievedMilestones(): Flow<List<Milestone>>

    @Query("SELECT * FROM milestones WHERE isAchieved = 1 ORDER BY achievedAt DESC")
    fun getAchievedMilestones(): Flow<List<Milestone>>

    @Query("SELECT * FROM milestones WHERE targetDays <= :days AND isAchieved = 0")
    suspend fun getMilestonesToAchieve(days: Int): List<Milestone>

    // Motivational Quotes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: MotivationalQuote): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<MotivationalQuote>)

    @Query("SELECT * FROM motivational_quotes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuote(): MotivationalQuote?

    @Query("SELECT COUNT(*) FROM motivational_quotes")
    suspend fun getQuoteCount(): Int
}
