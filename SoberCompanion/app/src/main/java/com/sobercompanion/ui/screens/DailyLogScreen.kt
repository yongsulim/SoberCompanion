package com.sobercompanion.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sobercompanion.SoberCompanionApp
import com.sobercompanion.data.repository.SobrietyRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLogScreen(
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sobrietyRepository = SobrietyRepository(SoberCompanionApp.instance.database.sobrietyDao())
    val snackbarHostState = remember { SnackbarHostState() }

    var mood by remember { mutableFloatStateOf(3f) }
    var cravingLevel by remember { mutableFloatStateOf(1f) }
    var didDrink by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }

    val today = LocalDate.now()

    LaunchedEffect(Unit) {
        sobrietyRepository.getDailyLogByDate(today)?.let { existingLog ->
            mood = existingLog.mood.toFloat()
            cravingLevel = existingLog.cravingLevel.toFloat()
            didDrink = existingLog.didDrink
            note = existingLog.note
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("오늘의 기록") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Date
            Text(
                text = today.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mood
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "오늘 기분은 어떠셨나요?",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("매우 나쁨", style = MaterialTheme.typography.bodySmall)
                        Text("보통", style = MaterialTheme.typography.bodySmall)
                        Text("매우 좋음", style = MaterialTheme.typography.bodySmall)
                    }
                    Slider(
                        value = mood,
                        onValueChange = { mood = it },
                        valueRange = 1f..5f,
                        steps = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = getMoodText(mood.toInt()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Craving Level
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "음주 욕구는 얼마나 있었나요?",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("전혀 없음", style = MaterialTheme.typography.bodySmall)
                        Text("보통", style = MaterialTheme.typography.bodySmall)
                        Text("매우 강함", style = MaterialTheme.typography.bodySmall)
                    }
                    Slider(
                        value = cravingLevel,
                        onValueChange = { cravingLevel = it },
                        valueRange = 1f..5f,
                        steps = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = getCravingText(cravingLevel.toInt()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Did Drink
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "오늘 음주 여부",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !didDrink,
                            onClick = { didDrink = false },
                            label = { Text("마시지 않음") }
                        )
                        FilterChip(
                            selected = didDrink,
                            onClick = { didDrink = true },
                            label = { Text("마심") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("오늘의 메모 (선택)") },
                placeholder = { Text("오늘 있었던 일이나 느낌을 기록해보세요") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    scope.launch {
                        sobrietyRepository.saveDailyLog(
                            date = today,
                            mood = mood.toInt(),
                            cravingLevel = cravingLevel.toInt(),
                            didDrink = didDrink,
                            note = note
                        )

                        if (didDrink) {
                            sobrietyRepository.resetSobriety()
                        }

                        snackbarHostState.showSnackbar("기록이 저장되었습니다")
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("저장하기", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

private fun getMoodText(mood: Int): String {
    return when (mood) {
        1 -> "매우 나빴어요"
        2 -> "조금 안 좋았어요"
        3 -> "보통이었어요"
        4 -> "좋았어요"
        5 -> "매우 좋았어요"
        else -> ""
    }
}

private fun getCravingText(level: Int): String {
    return when (level) {
        1 -> "전혀 없었어요"
        2 -> "조금 있었어요"
        3 -> "보통이었어요"
        4 -> "강했어요"
        5 -> "매우 강했어요"
        else -> ""
    }
}
