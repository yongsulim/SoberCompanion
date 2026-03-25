import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

val keystoreProperties = Properties().also { props ->
    rootProject.file("keystore.properties").takeIf { it.exists() }
        ?.inputStream()?.use { props.load(it) }
}

android {
    namespace = "com.sobercompanion"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sobercompanion"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // 한국어 + 영어만 포함 (80개 언어 리소스 제거 → 용량 대폭 감소)
        resourceConfigurations += listOf("ko", "en")
    }

    signingConfigs {
        create("release") {
            storeFile     = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias      = keystoreProperties["keyAlias"] as String
            keyPassword   = keystoreProperties["keyPassword"] as String
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // R8 코드 축소 + 난독화
            isMinifyEnabled = true
            // 사용하지 않는 리소스 제거
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        // 사용하지 않는 빌드 기능 비활성화 (빌드 시간 단축)
        buildConfig = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
    }

    // Kotlin 2.0부터 Compose 컴파일러가 플러그인으로 통합 → composeOptions 불필요

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/DEPENDENCIES",
                "/META-INF/LICENSE*",
                "/META-INF/NOTICE*",
                "/META-INF/*.kotlin_module",
                "DebugProbesKt.bin",
                "kotlin-tooling-metadata.json",
                "*.proto",
            )
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.09.03"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    // material-icons-extended: R8가 실제 사용 아이콘(EmojiEvents 등)만 남기고 제거
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-text-google-fonts")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.2")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.03"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
