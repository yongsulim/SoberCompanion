package com.sobercompanion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "sobriety_records")
data class SobrietyRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime? = null,
    val isActive: Boolean = true,
    val reason: String = "",
    val note: String = ""
)

@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val mood: Int, // 1-5 scale
    val cravingLevel: Int, // 1-5 scale
    val didDrink: Boolean = false,
    val drinkAmount: Int = 0, // in standard drinks
    val note: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity(tableName = "milestones")
data class Milestone(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val targetDays: Int,
    val achievedAt: LocalDateTime? = null,
    val isAchieved: Boolean = false
)

@Entity(tableName = "motivational_quotes")
data class MotivationalQuote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quote: String,
    val author: String = ""
)
