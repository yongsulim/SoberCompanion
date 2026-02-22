package com.sobercompanion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 금주 기록 엔티티. 금주 시작부터 종료까지 하나의 레코드를 나타냅니다.
 *
 * 사용 흐름:
 * - 온보딩 완료 시 isActive=true로 새 레코드 생성
 * - 음주(FAIL) 기록 시 현재 레코드의 endDate를 설정하고 isActive=false로 변경
 * - 새 레코드가 다시 isActive=true로 생성됨
 *
 * 테이블명: sobriety_records
 */
@Entity(tableName = "sobriety_records")
data class SobrietyRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 이번 금주 시작 시각 */
    val startDate: LocalDateTime,

    /** 금주 종료 시각. null이면 현재 진행 중인 기록 */
    val endDate: LocalDateTime? = null,

    /** 현재 활성화된 금주 기록 여부. 한 번에 하나만 true일 수 있음 */
    val isActive: Boolean = true,

    /** 사용자가 입력한 금주 목표 이유 */
    val reason: String = "",

    /** 추가 메모 */
    val note: String = ""
)

/**
 * 일일 기분/욕구 기록 엔티티. 하루에 하나씩 생성됩니다.
 *
 * DailyLogScreen에서 작성하며, 같은 날짜로 다시 저장하면 REPLACE 전략에 의해 덮어씁니다.
 * StatisticsScreen의 트렌드 차트 및 집계 통계에 사용됩니다.
 *
 * 테이블명: daily_logs
 */
@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 기록 날짜 (하루 하나) */
    val date: LocalDate,

    /** 오늘 기분 점수: 1(매우 나쁨) ~ 5(매우 좋음) */
    val mood: Int,

    /** 음주 욕구 수준: 1(전혀 없음) ~ 5(매우 강함) */
    val cravingLevel: Int,

    /** 오늘 음주 여부 */
    val didDrink: Boolean = false,

    /** 오늘 음주량 (표준잔 기준). didDrink=false이면 0 */
    val drinkAmount: Int = 0,

    /** 오늘의 자유 메모 */
    val note: String = "",

    /** 기록 생성 시각 (자동 설정) */
    val createdAt: LocalDateTime = LocalDateTime.now()
)

/**
 * 마일스톤 달성 기록 엔티티.
 *
 * 앱 최초 실행 시 SoberDatabase.DatabaseCallback에서
 * 1·3·7·14·30·60·90·180·365일 총 9개의 기본 마일스톤이 삽입됩니다.
 * 금주 일수가 targetDays에 도달하면 isAchieved=true, achievedAt이 기록됩니다.
 *
 * 테이블명: milestones
 */
@Entity(tableName = "milestones")
data class Milestone(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 마일스톤 이름 (예: "일주일 챔피언") */
    val title: String,

    /** 마일스톤 설명 (예: "금주 7일 달성!") */
    val description: String,

    /** 달성 목표 일수 */
    val targetDays: Int,

    /** 달성 시각. null이면 아직 미달성 */
    val achievedAt: LocalDateTime? = null,

    /** 달성 여부 플래그 */
    val isAchieved: Boolean = false
)

/**
 * 동기 부여 명언 엔티티.
 *
 * 홈 화면 하단 "오늘의 한마디" 섹션에 랜덤으로 표시됩니다.
 * 앱 최초 실행 시 기본 명언 10개가 삽입됩니다.
 * author가 빈 문자열이면 출처 없이 명언만 표시됩니다.
 *
 * 테이블명: motivational_quotes
 */
@Entity(tableName = "motivational_quotes")
data class MotivationalQuote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 명언 본문 */
    val quote: String,

    /** 명언 출처/저자. 없으면 빈 문자열 */
    val author: String = ""
)
