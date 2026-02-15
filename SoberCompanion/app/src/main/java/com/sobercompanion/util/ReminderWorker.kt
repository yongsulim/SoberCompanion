package com.sobercompanion.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sobercompanion.MainActivity
import com.sobercompanion.R
import com.sobercompanion.SoberCompanionApp
import com.sobercompanion.data.repository.SobrietyRepository
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val sobrietyRepository = SobrietyRepository(
            SoberCompanionApp.instance.database.sobrietyDao()
        )

        val activeRecord = sobrietyRepository.activeSobrietyRecord.firstOrNull()
        val soberDays = activeRecord?.let {
            ChronoUnit.DAYS.between(it.startDate, LocalDateTime.now()).toInt()
        } ?: 0

        val quote = sobrietyRepository.getRandomQuote()

        val message = buildString {
            append("금주 ${soberDays}일째입니다! ")
            if (quote != null) {
                append(quote.quote)
            } else {
                append("오늘도 화이팅!")
            }
        }

        showNotification(message)

        return Result.success()
    }

    private fun showNotification(message: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            SoberCompanionApp.NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("금주 동반자")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(applicationContext).notify(
                NOTIFICATION_ID,
                notification
            )
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val WORK_NAME = "daily_reminder_work"

        fun scheduleDailyReminder(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                24, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun cancelDailyReminder(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
