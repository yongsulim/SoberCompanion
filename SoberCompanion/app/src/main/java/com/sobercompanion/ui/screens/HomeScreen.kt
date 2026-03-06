package com.sobercompanion.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sobercompanion.data.RecordStatus
import com.sobercompanion.ui.components.ComfortMessageCard
import com.sobercompanion.util.ComfortMessageProvider
import com.sobercompanion.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val successMessages = listOf(
    "잘 버텼어요 👍",
    "기억해둘게요"
)

private val shakyMessages = listOf(
    "심호흡 한번 해봐요",
    "잠시 일어나서 물 한잔 가져와 보는 건 어때요?",
    "눈을 감고 다른 할 일을 떠올려 보는 건 어때요?"
)

private val drinkMessages = listOf(
    "괜찮아요",
    "다시 시작하면 돼요"
)

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // '지금 좀 흔들려' 버튼 깜빡임 애니메이션
    val infiniteTransition = rememberInfiniteTransition(label = "shaky_blink")
    val shakyBlinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shakyBlinkAlpha"
    )

    // 오늘 기록된 버튼 판별
    val successSelected = uiState.hasRecordedToday && uiState.dailyStatus == RecordStatus.SUCCESS
    val failSelected = uiState.hasRecordedToday && uiState.dailyStatus == RecordStatus.FAIL

    fun showMessage(message: String) {
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val job = launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Indefinite
                )
            }
            delay(3000)
            job.cancel()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MainActionButton(
                    text = "오늘은 버텼어",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    enabled = !uiState.hasRecordedToday,
                    isSelected = successSelected,
                    onClick = {
                        mainViewModel.onTodaySuccess()
                        showMessage(successMessages.random())
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 깜빡임: containerColor에 직접 alpha 적용
                MainActionButton(
                    text = "지금 좀 흔들려",
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = shakyBlinkAlpha),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    enabled = true,
                    isSelected = true,
                    onClick = {
                        mainViewModel.onShaky()
                        showMessage(shakyMessages.random())
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                MainActionButton(
                    text = "마셨어",
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    enabled = !uiState.hasRecordedToday,
                    isSelected = failSelected,
                    onClick = {
                        mainViewModel.onDrink()
                        showMessage(drinkMessages.random())
                    }
                )
            }
        }

        ComfortMessageCard(
            show = uiState.comfortReady && !uiState.comfortShown,
            message = ComfortMessageProvider.getMessage(uiState.shakyCountToday),
            onDismiss = { mainViewModel.onComfortMessageSeen() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                shape = RoundedCornerShape(12.dp),
                containerColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.93f),
                contentColor = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
    }
}

@Composable
private fun MainActionButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    enabled: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            // 기본(미기록): 흐리게
            containerColor = if (isSelected) containerColor else containerColor.copy(alpha = 0.4f),
            contentColor = if (isSelected) contentColor else contentColor.copy(alpha = 0.4f),
            // 비활성: 기록된 버튼은 선명하게, 나머지는 더 흐리게
            disabledContainerColor = if (isSelected) containerColor else containerColor.copy(alpha = 0.2f),
            disabledContentColor = if (isSelected) contentColor else contentColor.copy(alpha = 0.3f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
