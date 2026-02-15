package com.sobercompanion

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.sobercompanion.data.local.SoberDatabase

class SoberCompanionApp : Application() {

    lateinit var database: SoberDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = SoberDatabase.getInstance(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "금주 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "금주 기록 및 응원 알림"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "sober_companion_channel"

        lateinit var instance: SoberCompanionApp
            private set
    }
}
