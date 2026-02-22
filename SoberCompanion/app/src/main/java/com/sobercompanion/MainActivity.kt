package com.sobercompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sobercompanion.ui.SoberCompanionNavHost
import com.sobercompanion.ui.theme.SoberCompanionTheme

/**
 * 앱의 유일한 Activity. 싱글 액티비티 패턴을 사용합니다.
 *
 * 화면 전환은 Jetpack Navigation Compose로 처리되므로
 * 이 Activity에는 UI 로직이 거의 없습니다.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 상태바/내비게이션바 영역까지 콘텐츠를 확장 (Edge-to-Edge)
        enableEdgeToEdge()

        setContent {
            SoberCompanionTheme {
                // 배경색을 MaterialTheme으로 통일
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 앱 전체 네비게이션 그래프 진입점
                    SoberCompanionNavHost()
                }
            }
        }
    }
}
