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

/**
 * Room 데이터베이스의 모든 쿼리를 정의하는 Data Access Object.
 *
 * Flow를 반환하는 쿼리는 데이터 변경 시 자동으로 새 값을 emit합니다.
 * suspend 함수는 일회성 읽기/쓰기에 사용됩니다.
 *
 * OnConflictStrategy.REPLACE: 같은 PK가 있으면 기존 행을 삭제하고 새로 삽입합니다.
 */
@Dao
interface SobrietyDao {

    // ========== Sobriety Records ==========

    /**
     * 새 금주 기록을 삽입하거나 기존 기록을 업데이트합니다.
     * @return 삽입된 행의 ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSobrietyRecord(record: SobrietyRecord): Long

    @Update
    suspend fun updateSobrietyRecord(record: SobrietyRecord)

    @Delete
    suspend fun deleteSobrietyRecord(record: SobrietyRecord)

    /**
     * 현재 활성화된 금주 기록을 Flow로 반환합니다.
     * isActive=1인 레코드는 항상 하나여야 합니다.
     * null이면 아직 추적이 시작되지 않은 상태입니다.
     */
    @Query("SELECT * FROM sobriety_records WHERE isActive = 1 LIMIT 1")
    fun getActiveSobrietyRecord(): Flow<SobrietyRecord?>

    /** 모든 금주 기록을 최신순으로 반환합니다 (통계/이력 조회용) */
    @Query("SELECT * FROM sobriety_records ORDER BY startDate DESC")
    fun getAllSobrietyRecords(): Flow<List<SobrietyRecord>>

    /**
     * 현재 활성 기록을 종료합니다 (음주 기록 시 호출).
     * isActive=0으로 변경하고 endDate를 기록합니다.
     */
    @Query("UPDATE sobriety_records SET isActive = 0, endDate = :endDate WHERE isActive = 1")
    suspend fun endCurrentSobriety(endDate: java.time.LocalDateTime)

    // ========== Daily Logs ==========

    /**
     * 일일 기록을 삽입하거나 같은 날짜의 기존 기록을 덮어씁니다.
     * DailyLogScreen에서 저장 버튼 클릭 시 호출됩니다.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLog(log: DailyLog): Long

    @Update
    suspend fun updateDailyLog(log: DailyLog)

    @Delete
    suspend fun deleteDailyLog(log: DailyLog)

    /**
     * 특정 날짜의 일일 기록을 조회합니다.
     * DailyLogScreen 진입 시 기존 기록 여부를 확인하는 데 사용됩니다.
     */
    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    suspend fun getDailyLogByDate(date: LocalDate): DailyLog?

    /** 모든 일일 기록을 최신순으로 Flow로 반환합니다 */
    @Query("SELECT * FROM daily_logs ORDER BY date DESC")
    fun getAllDailyLogs(): Flow<List<DailyLog>>

    /**
     * 특정 기간의 일일 기록을 날짜 오름차순으로 반환합니다.
     * 트렌드 차트 등 기간별 분석에 사용됩니다.
     */
    @Query("SELECT * FROM daily_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getDailyLogsBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyLog>>

    /**
     * 최근 N개의 일일 기록을 최신순으로 반환합니다.
     * StatisticsScreen의 7일 트렌드 조회에 사용됩니다.
     *
     * @param limit 가져올 최대 기록 수
     */
    @Query("SELECT * FROM daily_logs ORDER BY date DESC LIMIT :limit")
    fun getRecentDailyLogs(limit: Int): Flow<List<DailyLog>>

    // ========== Milestones ==========

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: Milestone): Long

    /** 여러 마일스톤을 한 번에 삽입합니다 (초기 데이터 삽입 시 사용) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<Milestone>)

    @Update
    suspend fun updateMilestone(milestone: Milestone)

    /** 모든 마일스톤을 목표 일수 오름차순으로 반환합니다 */
    @Query("SELECT * FROM milestones ORDER BY targetDays ASC")
    fun getAllMilestones(): Flow<List<Milestone>>

    /** 아직 달성하지 못한 마일스톤을 목표 일수 오름차순으로 반환합니다 */
    @Query("SELECT * FROM milestones WHERE isAchieved = 0 ORDER BY targetDays ASC")
    fun getUnachievedMilestones(): Flow<List<Milestone>>

    /** 달성한 마일스톤을 달성 시각 최신순으로 반환합니다 */
    @Query("SELECT * FROM milestones WHERE isAchieved = 1 ORDER BY achievedAt DESC")
    fun getAchievedMilestones(): Flow<List<Milestone>>

    /**
     * 현재 금주 일수로 새로 달성 가능한 마일스톤 목록을 조회합니다.
     * 금주 일수가 업데이트될 때마다 호출해 isAchieved를 true로 업데이트합니다.
     *
     * @param days 현재 금주 일수
     */
    @Query("SELECT * FROM milestones WHERE targetDays <= :days AND isAchieved = 0")
    suspend fun getMilestonesToAchieve(days: Int): List<Milestone>

    // ========== Motivational Quotes ==========

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: MotivationalQuote): Long

    /** 여러 명언을 한 번에 삽입합니다 (초기 데이터 삽입 시 사용) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<MotivationalQuote>)

    /**
     * DB에서 랜덤으로 명언 하나를 조회합니다.
     * 홈 화면 "오늘의 한마디" 섹션에서 사용됩니다.
     * 명언이 없으면 null을 반환합니다.
     */
    @Query("SELECT * FROM motivational_quotes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuote(): MotivationalQuote?

    /** 저장된 명언 총 개수를 반환합니다 (초기 데이터 중복 삽입 방지에 사용) */
    @Query("SELECT COUNT(*) FROM motivational_quotes")
    suspend fun getQuoteCount(): Int
}
