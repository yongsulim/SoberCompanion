package com.sobercompanion

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.sobercompanion.data.local.SoberDatabase

/**
 * 앱 전체 수명주기를 관리하는 Application 클래스.
 *
 * 역할:
 * - Room 데이터베이스 싱글턴 초기화
 * - Android 8.0(Oreo) 이상에서 알림 채널 생성
 * - 전역 인스턴스 제공 (WorkManager 콜백 등에서 Context 필요 시)
 *
 * AndroidManifest.xml의 android:name 속성에 등록되어 있습니다.
 */
class SoberCompanionApp : Application() {

    /** 앱 전체에서 공유하는 Room DB 인스턴스 */
    lateinit var database: SoberDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        // 전역 참조 설정 (WorkManager 등 Context 없는 곳에서 사용)
        instance = this

        // DB는 앱 시작 시 한 번만 초기화 (Singleton)
        database = SoberDatabase.getInstance(this)

        // Android O(API 26) 이상에서는 알림 채널이 필수
        createNotificationChannel()
    }

    /**
     * 알림 채널을 생성합니다.
     * Android 8.0(API 26) 미만에서는 채널 개념이 없으므로 분기 처리합니다.
     *
     * 생성된 채널: "금주 알림" (중요도: 기본)
     * - 흔들림 후 위로 메시지 알림에 사용 (현재 버전에서는 앱 내 카드로 표시)
     * - 매일 저녁 리마인더 알림에 사용
     */
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
        /** WorkManager/Worker에서 알림 전송 시 사용하는 채널 ID */
        const val NOTIFICATION_CHANNEL_ID = "sober_companion_channel"

        /**
         * Application 인스턴스 전역 참조.
         * Activity 없이 Context가 필요한 곳(Worker 등)에서만 사용하세요.
         */
        lateinit var instance: SoberCompanionApp
            private set
    }
}
