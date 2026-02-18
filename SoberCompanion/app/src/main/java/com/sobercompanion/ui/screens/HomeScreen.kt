package com.sobercompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sobercompanion.SoberCompanionApp
import com.sobercompanion.data.datastore.UserPreferencesRepository
import com.sobercompanion.data.local.entity.MotivationalQuote
import com.sobercompanion.data.repository.SobrietyRepository
import com.sobercompanion.ui.components.ComfortMessageCard
import com.sobercompanion.util.ComfortMessageProvider
import com.sobercompanion.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDailyLog: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToMilestones: () -> Unit,
    onNavigateToSettings: () -> Unit,
    mainViewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferencesRepository = UserPreferencesRepository(context)
    val sobrietyRepository = SobrietyRepository(SoberCompanionApp.instance.database.sobrietyDao())
    val mainUiState by mainViewModel.uiState.collectAsState()

    val userName by userPreferencesRepository.userName.collectAsState(initial = "")
    val activeSobriety by sobrietyRepository.activeSobrietyRecord.collectAsState(initial = null)

    var quote by remember { mutableStateOf<MotivationalQuote?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        quote = sobrietyRepository.getRandomQuote()
    }

    // 날짜 변경 감지: 30초마다 현재 날짜를 확인하여 자정이 지났으면 리셋
    val lastCheckedDate by mainViewModel.lastCheckedDate.collectAsState()
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            val today = LocalDate.now()
            if (lastCheckedDate.isBefore(today)) {
                mainViewModel.checkDateChange()
            }
        }
    }

    val soberDays = activeSobriety?.let {
        ChronoUnit.DAYS.between(it.startDate, LocalDateTime.now()).toInt()
    } ?: 0

    val soberHours = activeSobriety?.let {
        ChronoUnit.HOURS.between(it.startDate, LocalDateTime.now()) % 24
    } ?: 0

    LaunchedEffect(soberDays) {
        if (soberDays > 0) {
            sobrietyRepository.checkAndUpdateMilestones(soberDays)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("금주 동반자") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "설정")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToDailyLog
            ) {
                Icon(Icons.Default.Add, contentDescription = "오늘 기록하기")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Comfort Message Card
            if (mainUiState.comfortReady && !mainUiState.comfortShown) {
                ComfortMessageCard(
                    message = ComfortMessageProvider.getMessage(mainUiState.shakyCountToday),
                    onDismiss = { mainViewModel.onComfortMessageSeen() }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Greeting
            if (userName.isNotEmpty()) {
                Text(
                    text = "${userName}님, 안녕하세요!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Sober Days Counter
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "금주",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "$soberDays",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "일",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "$soberHours",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "시간",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = getEncouragementMessage(soberDays),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    onClick = onNavigateToStatistics,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "통계",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }

                Card(
                    onClick = onNavigateToMilestones,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "업적",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }

                Card(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "리셋",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Motivational Quote
            quote?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "오늘의 한마디",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "\"${it.quote}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (it.author.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "- ${it.author}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }

    // Reset Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("금주 리셋") },
            text = {
                Text("정말 금주 기록을 리셋하시겠습니까?\n괜찮아요, 다시 시작하면 됩니다.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            sobrietyRepository.resetSobriety()
                            showResetDialog = false
                        }
                    }
                ) {
                    Text("리셋", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

private fun getEncouragementMessage(days: Int): String {
    return when {
        days == 0 -> "오늘부터 시작이에요!"
        days == 1 -> "첫 날을 넘겼어요!"
        days < 7 -> "잘하고 있어요!"
        days < 14 -> "일주일 넘었어요! 대단해요!"
        days < 30 -> "2주 이상! 놀라워요!"
        days < 60 -> "한 달 넘었어요! 최고예요!"
        days < 90 -> "2달 이상! 정말 대단해요!"
        days < 180 -> "3달 넘었어요! 자랑스러워요!"
        days < 365 -> "반년 이상! 영웅이에요!"
        else -> "1년 이상! 전설이에요!"
    }
}
