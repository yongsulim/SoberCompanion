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
-dontwarn kotlin.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ============================================================
# WorkManager
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
# Compose
# ============================================================
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# ============================================================
# App 클래스 보호 (Data 모델)
# ============================================================
-keep class com.sobercompanion.data.** { *; }
-keep class com.sobercompanion.util.** { *; }
