package com.sobercompanion.data.local

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Room이 SQLite에 저장할 수 없는 Java/Kotlin 타입을 변환하는 TypeConverter.
 *
 * Room은 기본적으로 String, Int, Long, Double, Boolean만 저장할 수 있습니다.
 * LocalDate와 LocalDateTime은 ISO-8601 형식의 문자열로 변환해 TEXT 컬럼에 저장합니다.
 *
 * 저장 형식:
 * - LocalDateTime: "2024-01-15T10:30:00" (ISO_LOCAL_DATE_TIME)
 * - LocalDate: "2024-01-15" (ISO_LOCAL_DATE)
 *
 * SoberDatabase 클래스의 @TypeConverters 어노테이션에 등록되어 있습니다.
 */
class Converters {
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /** LocalDateTime → String (DB에 저장할 때) */
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(dateTimeFormatter)
    }

    /** String → LocalDateTime (DB에서 읽을 때) */
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, dateTimeFormatter) }
    }

    /** LocalDate → String (DB에 저장할 때) */
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.format(dateFormatter)
    }

    /** String → LocalDate (DB에서 읽을 때) */
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, dateFormatter) }
    }
}
