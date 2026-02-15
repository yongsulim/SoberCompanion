package com.sobercompanion.data.repository

import com.sobercompanion.data.local.dao.SobrietyDao
import com.sobercompanion.data.local.entity.DailyLog
import com.sobercompanion.data.local.entity.Milestone
import com.sobercompanion.data.local.entity.MotivationalQuote
import com.sobercompanion.data.local.entity.SobrietyRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

class SobrietyRepository(private val dao: SobrietyDao) {

    // Sobriety Records
    val activeSobrietyRecord: Flow<SobrietyRecord?> = dao.getActiveSobrietyRecord()
    val allSobrietyRecords: Flow<List<SobrietyRecord>> = dao.getAllSobrietyRecords()

    suspend fun startNewSobriety(reason: String = "", note: String = ""): Long {
        // End any active sobriety first
        dao.endCurrentSobriety(LocalDateTime.now())

        val record = SobrietyRecord(
            startDate = LocalDateTime.now(),
            reason = reason,
            note = note,
            isActive = true
        )
        return dao.insertSobrietyRecord(record)
    }

    suspend fun endCurrentSobriety() {
        dao.endCurrentSobriety(LocalDateTime.now())
    }

    suspend fun resetSobriety(reason: String = "") {
        dao.endCurrentSobriety(LocalDateTime.now())
        startNewSobriety(reason)
    }

    // Daily Logs
    val allDailyLogs: Flow<List<DailyLog>> = dao.getAllDailyLogs()

    fun getRecentDailyLogs(limit: Int = 7): Flow<List<DailyLog>> = dao.getRecentDailyLogs(limit)

    fun getDailyLogsBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyLog>> =
        dao.getDailyLogsBetween(startDate, endDate)

    suspend fun getDailyLogByDate(date: LocalDate): DailyLog? = dao.getDailyLogByDate(date)

    suspend fun saveDailyLog(
        date: LocalDate,
        mood: Int,
        cravingLevel: Int,
        didDrink: Boolean = false,
        drinkAmount: Int = 0,
        note: String = ""
    ): Long {
        val existingLog = dao.getDailyLogByDate(date)
        val log = DailyLog(
            id = existingLog?.id ?: 0,
            date = date,
            mood = mood,
            cravingLevel = cravingLevel,
            didDrink = didDrink,
            drinkAmount = drinkAmount,
            note = note
        )
        return if (existingLog != null) {
            dao.updateDailyLog(log)
            log.id
        } else {
            dao.insertDailyLog(log)
        }
    }

    // Milestones
    val allMilestones: Flow<List<Milestone>> = dao.getAllMilestones()
    val unachievedMilestones: Flow<List<Milestone>> = dao.getUnachievedMilestones()
    val achievedMilestones: Flow<List<Milestone>> = dao.getAchievedMilestones()

    suspend fun checkAndUpdateMilestones(soberDays: Int) {
        val milestonesToAchieve = dao.getMilestonesToAchieve(soberDays)
        milestonesToAchieve.forEach { milestone ->
            dao.updateMilestone(
                milestone.copy(
                    isAchieved = true,
                    achievedAt = LocalDateTime.now()
                )
            )
        }
    }

    // Motivational Quotes
    suspend fun getRandomQuote(): MotivationalQuote? = dao.getRandomQuote()
}
