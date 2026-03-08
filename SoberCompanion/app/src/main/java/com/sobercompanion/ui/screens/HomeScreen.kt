package com.sobercompanion.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sobercompanion.ui.components.ComfortMessageCard
import com.sobercompanion.ui.theme.AppBackground
import com.sobercompanion.ui.theme.AppBorder
import com.sobercompanion.ui.theme.AppSurface
import com.sobercompanion.ui.theme.AppSurface2
import com.sobercompanion.ui.theme.AppTextPrimary
import com.sobercompanion.ui.theme.AppTextSecondary
import com.sobercompanion.ui.theme.AppTextTertiary
import com.sobercompanion.ui.theme.NotoSansKr
import com.sobercompanion.ui.theme.NotoSerifKr
import com.sobercompanion.util.ComfortMessageProvider
import com.sobercompanion.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState by mainViewModel.uiState.collectAsState()

    // 지금 좀 흔들려 버튼 우측 dot — 숨쉬는 애니메이션 (2.8초 주기)
    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 0.35f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )
    val dotScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 0.7f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
    ) {
        // ── 위로 메시지 카드 (상단, 조건부 표시) ─────────────────────────────
        ComfortMessageCard(
            show      = uiState.comfortReady && !uiState.comfortShown,
            message   = ComfortMessageProvider.getMessage(uiState.shakyCountToday),
            onDismiss = { mainViewModel.onComfortMessageSeen() },
            modifier  = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp)
        )

        // ── 메인 콘텐츠 ───────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .padding(bottom = 32.dp)
        ) {
            Spacer(Modifier.height(52.dp))

            // 눈썹 텍스트 "연속"
            Text(
                text          = "연속",
                fontFamily    = NotoSansKr,
                fontWeight    = FontWeight.Normal,
                fontSize      = 11.sp,
                color         = AppTextTertiary,
                letterSpacing = 1.2.sp,
            )

            Spacer(Modifier.height(10.dp))

            // 연속 일수 숫자 + 단위
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text          = "${uiState.currentStreak}",
                    fontFamily    = NotoSerifKr,
                    fontWeight    = FontWeight.Light,
                    fontSize      = 48.sp,
                    lineHeight    = 48.sp,
                    color         = AppTextPrimary,
                    letterSpacing = (-2).sp,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text          = "일째 버티는 중",
                    fontFamily    = NotoSerifKr,
                    fontWeight    = FontWeight.Light,
                    fontSize      = 22.sp,
                    color         = AppTextSecondary,
                    letterSpacing = (-0.5).sp,
                    modifier      = Modifier.padding(bottom = 5.dp),
                )
            }

            Spacer(Modifier.height(10.dp))

            // 시작 날짜 부제
            val startText = uiState.startDate?.let {
                "${it.year}년 ${it.monthValue}월 ${it.dayOfMonth}일부터"
            } ?: ""
            if (startText.isNotEmpty()) {
                Text(
                    text          = startText,
                    fontFamily    = NotoSansKr,
                    fontWeight    = FontWeight.Light,
                    fontSize      = 12.5.sp,
                    color         = AppTextTertiary,
                    letterSpacing = (-0.1).sp,
                )
            }

            // 구분선
            Spacer(Modifier.height(36.dp))
            HorizontalDivider(thickness = 1.dp, color = AppBorder)
            Spacer(Modifier.height(28.dp))

            // 버튼 그룹 or "오늘 기록했어"
            if (uiState.hasRecordedToday) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp, bottom = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text          = "오늘 기록했어",
                        fontFamily    = NotoSerifKr,
                        fontWeight    = FontWeight.Light,
                        fontSize      = 17.sp,
                        color         = AppTextSecondary,
                        letterSpacing = (-0.4).sp,
                    )
                }
                ShakyCard(
                    shakyCount = uiState.shakyCountToday,
                    dotAlpha   = dotAlpha,
                    dotScale   = dotScale,
                    onClick    = { mainViewModel.onShaky() }
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                    ActionCard(
                        label   = "오늘은 버텼어",
                        onClick = { mainViewModel.onTodaySuccess() }
                    )
                    ShakyCard(
                        shakyCount = uiState.shakyCountToday,
                        dotAlpha   = dotAlpha,
                        dotScale   = dotScale,
                        onClick    = { mainViewModel.onShaky() }
                    )
                    ActionCard(
                        label   = "마셨어",
                        onClick = { mainViewModel.onDrink() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShakyCard(
    shakyCount: Int,
    dotAlpha: Float,
    dotScale: Float,
    onClick: () -> Unit
) {
    val context  = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ActionCard(
            label    = "지금 좀 흔들려",
            dotAlpha = dotAlpha,
            dotScale = dotScale,
            onClick  = {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE)
                )
                onClick()
            }
        )

        // 누른 횟수만큼 점 표시 (최대 5개)
        if (shakyCount > 0) {
            val dots = List(minOf(shakyCount, 5)) { "·" }.joinToString("  ")
            Text(
                text          = dots,
                fontFamily    = NotoSansKr,
                fontWeight    = FontWeight.Light,
                fontSize      = 12.sp,
                color         = AppTextTertiary,
                modifier      = Modifier.padding(top = 8.dp),
                letterSpacing = 0.sp,
            )
        }
    }
}

@Composable
private fun ActionCard(
    label: String,
    dotAlpha: Float = 1f,
    dotScale: Float = 1f,
    onClick: () -> Unit
) {
    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border    = BorderStroke(1.dp, AppBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 19.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text          = label,
                fontFamily    = NotoSansKr,
                fontWeight    = FontWeight.Normal,
                fontSize      = 15.5.sp,
                color         = AppTextPrimary,
                letterSpacing = (-0.4).sp,
            )
            // 우측 dot — 지금 좀 흔들려만 dotAlpha/dotScale 애니메이션 적용
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(dotScale)
                    .alpha(dotAlpha)
                    .clip(CircleShape)
                    .background(AppSurface2)
            )
        }
    }
}
