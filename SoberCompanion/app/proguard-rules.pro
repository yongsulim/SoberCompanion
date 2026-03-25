# ============================================================
# Room
# ============================================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-keepclassmembers @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# ============================================================
# Kotlin
# ============================================================
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes EnclosingMethod
-keep class kotlin.Metadata { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ============================================================
# WorkManager - Worker 클래스는 리플렉션으로 생성됨
# ============================================================
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keepnames class androidx.work.** { *; }

# ============================================================
# DataStore
# ============================================================
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite* {
    <fields>;
}

# ============================================================
# Compose - Kotlin 2.0 + R8 fullMode가 자체 처리, 별도 keep 불필요
# (이전의 -keep class androidx.compose.** 는 R8 최적화를 방해하므로 제거)
# ============================================================

# ============================================================
# App - WorkManager Worker만 명시적 보호 (나머지는 R8이 최적화)
# ============================================================
-keep class com.sobercompanion.workers.** {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
