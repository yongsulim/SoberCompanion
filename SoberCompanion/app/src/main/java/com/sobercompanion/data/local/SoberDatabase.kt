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
@TypeConverters(Converters::class)
abstract class SoberDatabase : RoomDatabase() {

    abstract fun sobrietyDao(): SobrietyDao

    companion object {
        @Volatile
        private var INSTANCE: SoberDatabase? = null

        fun getInstance(context: Context): SoberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoberDatabase::class.java,
                    "sober_companion_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.sobrietyDao())
                }
            }
        }

        private suspend fun populateDatabase(dao: SobrietyDao) {
            // Default milestones
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

            // Default motivational quotes
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
