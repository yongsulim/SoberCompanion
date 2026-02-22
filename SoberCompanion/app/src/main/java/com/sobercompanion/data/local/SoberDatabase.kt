package com.sobercompanion.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sobercompanion.data.local.dao.SobrietyDao
import com.sobercompanion.data.local.entity.DailyLog
import com.sobercompanion.data.local.entity.Milestone
import com.sobercompanion.data.local.entity.MotivationalQuote
import com.sobercompanion.data.local.entity.SobrietyRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 앱의 Room 데이터베이스 싱글턴 클래스.
 *
 * 포함된 테이블:
 * - sobriety_records: 금주 기록 (시작/종료 시각, 활성 여부)
 * - daily_logs: 일일 기분/욕구 기록
 * - milestones: 마일스톤 달성 현황
 * - motivational_quotes: 홈 화면에 표시할 명언
 *
 * 스키마 변경 시 version을 올리고 Migration을 추가해야 합니다.
 * exportSchema = false: 스키마 JSON 파일을 생성하지 않음 (CI 불필요 시)
 */
@Database(
    entities = [
        SobrietyRecord::class,
        DailyLog::class,
        Milestone::class,
        MotivationalQuote::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class) // LocalDate/LocalDateTime 변환
abstract class SoberDatabase : RoomDatabase() {

    /** 모든 테이블에 접근하는 DAO */
    abstract fun sobrietyDao(): SobrietyDao

    companion object {
        /**
         * @Volatile: 이 변수의 값이 메인 메모리에서 직접 읽고 쓰여
         * 멀티스레드 환경에서 최신 값을 항상 볼 수 있도록 보장합니다.
         */
        @Volatile
        private var INSTANCE: SoberDatabase? = null

        /**
         * DB 인스턴스를 반환합니다. 없으면 생성합니다.
         *
         * synchronized(this): 한 번에 하나의 스레드만 이 블록을 실행해
         * 두 스레드가 동시에 DB를 생성하는 경쟁 조건을 방지합니다.
         *
         * @param context applicationContext를 사용해 메모리 누수를 방지합니다
         */
        fun getInstance(context: Context): SoberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoberDatabase::class.java,
                    "sober_companion_db"  // DB 파일명
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * DB 생성 시 최초 1회 실행되는 콜백.
     * 기본 마일스톤과 명언을 삽입합니다.
     *
     * onCreate는 DB가 처음 생성될 때만 호출됩니다.
     * (앱을 삭제하고 재설치하면 다시 실행됩니다)
     */
    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // IO 디스패처에서 코루틴으로 비동기 실행 (UI 스레드 블로킹 방지)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.sobrietyDao())
                }
            }
        }

        /**
         * 기본 마일스톤 9개와 동기 부여 명언 10개를 삽입합니다.
         * 새 명언을 추가하고 싶으면 이 함수의 quotes 리스트에 항목을 추가하세요.
         * (단, 이미 설치된 앱에는 반영되지 않습니다 — DB 마이그레이션 필요)
         */
        private suspend fun populateDatabase(dao: SobrietyDao) {
            // 기본 마일스톤: 1일부터 1년까지 9단계
            val milestones = listOf(
                Milestone(title = "첫 발걸음", description = "금주 1일 달성!", targetDays = 1),
                Milestone(title = "3일의 기적", description = "금주 3일 달성!", targetDays = 3),
                Milestone(title = "일주일 챔피언", description = "금주 7일 달성!", targetDays = 7),
                Milestone(title = "2주 전사", description = "금주 14일 달성!", targetDays = 14),
                Milestone(title = "한 달의 승리", description = "금주 30일 달성!", targetDays = 30),
                Milestone(title = "60일 마스터", description = "금주 60일 달성!", targetDays = 60),
                Milestone(title = "90일 영웅", description = "금주 90일 달성!", targetDays = 90),
                Milestone(title = "반년의 결실", description = "금주 180일 달성!", targetDays = 180),
                Milestone(title = "1년의 전설", description = "금주 365일 달성!", targetDays = 365)
            )
            dao.insertMilestones(milestones)

            // 기본 동기 부여 명언 (홈 화면 "오늘의 한마디"에 랜덤 표시)
            val quotes = listOf(
                MotivationalQuote(quote = "오늘 하루도 잘 해냈습니다. 내일도 할 수 있어요!", author = ""),
                MotivationalQuote(quote = "변화는 불편함에서 시작됩니다.", author = ""),
                MotivationalQuote(quote = "어제보다 나은 오늘, 오늘보다 나은 내일.", author = ""),
                MotivationalQuote(quote = "포기하지 않는 한, 실패는 없습니다.", author = ""),
                MotivationalQuote(quote = "작은 진전도 여전히 진전입니다.", author = ""),
                MotivationalQuote(quote = "당신은 생각보다 강합니다.", author = ""),
                MotivationalQuote(quote = "매일 조금씩, 꾸준히.", author = ""),
                MotivationalQuote(quote = "건강한 습관이 건강한 인생을 만듭니다.", author = ""),
                MotivationalQuote(quote = "오늘의 선택이 내일의 나를 만듭니다.", author = ""),
                MotivationalQuote(quote = "힘든 순간이 지나면 더 강해진 내가 있습니다.", author = "")
            )
            dao.insertQuotes(quotes)
        }
    }
}
