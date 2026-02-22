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

/**
 * 흔들림 기록 후 3시간이 지났을 때 위로 메시지 표시를 준비하는 WorkManager Worker.
 *
 * 동작 방식:
 * 1. onShaky() 호출 시 schedule()을 통해 3시간 딜레이로 예약됨
 * 2. 3시간 후 doWork()가 실행되어 comfort_ready_flag = true로 설정
 * 3. UI(HomeScreen)가 이 플래그를 감지해 ComfortMessageCard를 표시
 *
 * 푸시 알림 없이 앱 내 카드 방식으로만 위로 메시지를 표시합니다.
 * 앱이 종료되어도 WorkManager가 백그라운드에서 실행을 보장합니다.
 */
class ComfortMessageWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    /**
     * 3시간 후 실행될 백그라운드 작업.
     *
     * 이미 위로 메시지를 확인했으면 중복 실행을 방지합니다.
     * (사용자가 성공 기록을 한 뒤에도 예약된 Work가 실행될 수 있기 때문)
     */
    override suspend fun doWork(): Result {
        val dataStore = AppDataStore(applicationContext)

        // 사용자가 이미 위로 메시지를 확인했으면 플래그를 설정하지 않음
        val alreadyShown = dataStore.comfortMessageShown.first()
        if (alreadyShown) {
            return Result.success()
        }

        // UI에서 감지할 수 있도록 플래그만 설정 (푸시 알림 없음)
        dataStore.setComfortReadyFlag(true)

        return Result.success()
    }

    companion object {
        /** WorkManager에서 이 작업을 식별하는 고유 이름. 중복 예약 방지에 사용됨 */
        private const val WORK_NAME = "comfort_message_work"

        /**
         * 3시간 딜레이 후 위로 메시지 플래그를 설정하는 작업을 예약합니다.
         *
         * ExistingWorkPolicy.REPLACE: 이미 예약된 작업이 있으면 취소하고 새로 예약합니다.
         * (흔들림 버튼을 여러 번 누르면 타이머가 매번 3시간으로 초기화됨)
         *
         * @param context WorkManager에 접근하기 위한 Context
         */
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

        /**
         * 예약된 위로 메시지 작업을 취소합니다.
         * 사용자가 성공/음주를 기록해 타이머가 불필요해질 때 호출됩니다.
         *
         * @param context WorkManager에 접근하기 위한 Context
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
