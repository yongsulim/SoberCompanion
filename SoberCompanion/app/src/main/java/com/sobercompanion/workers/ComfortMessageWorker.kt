package com.sobercompanion.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sobercompanion.data.AppDataStore
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class ComfortMessageWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dataStore = AppDataStore(applicationContext)

        // 이미 위로 메시지를 본 경우 중복 실행 방지
        val alreadyShown = dataStore.comfortMessageShown.first()
        if (alreadyShown) {
            return Result.success()
        }

        // comfort_ready_flag만 true로 설정 (푸시 알림 없음)
        dataStore.setComfortReadyFlag(true)

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "comfort_message_work"

        fun schedule(context: Context) {
            val request = OneTimeWorkRequestBuilder<ComfortMessageWorker>()
                .setInitialDelay(3, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
