package com.sobercompanion.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.sobercompanion.SoberCompanionApp
import com.sobercompanion.data.local.entity.DailyLog
import com.sobercompanion.data.repository.SobrietyRepository
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit
) {
    val sobrietyRepository = SobrietyRepository(SoberCompanionApp.instance.database.sobrietyDao())
    val recentLogs by sobrietyRepository.getRecentDailyLogs(7).collectAsState(initial = emptyList())
    val allRecords by sobrietyRepository.allSobrietyRecords.collectAsState(initial = emptyList())

    val totalSoberDays = allRecords.sumOf { record ->
        val endDate = record.endDate ?: java.time.LocalDateTime.now()
        ChronoUnit.DAYS.between(record.startDate, endDate).toInt()
    }

    val longestStreak = allRecords.maxOfOrNull { record ->
        val endDate = record.endDate ?: java.time.LocalDateTime.now()
        ChronoUnit.DAYS.between(record.startDate, endDate).toInt()
    } ?: 0

    val averageMood = if (recentLogs.isNotEmpty()) {
        recentLogs.map { it.mood }.average()
    } else 0.0

    val averageCraving = if (recentLogs.isNotEmpty()) {
        recentLogs.map { it.cravingLevel }.average()
    } else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("통계") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Summary Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "총 금주일",
                    value = "$totalSoberDays",
                    unit = "일",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "최장 연속",
                    value = "$longestStreak",
                    unit = "일",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "평균 기분",
                    value = String.format("%.1f", averageMood),
                    unit = "/ 5",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "평균 음주욕구",
                    value = String.format("%.1f", averageCraving),
                    unit = "/ 5",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mood Chart
            if (recentLogs.isNotEmpty()) {
                Text(
                    text = "최근 7일 기분 추이",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                MoodChart(
                    logs = recentLogs.sortedBy { it.date },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Logs
                Text(
                    text = "최근 기록",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                recentLogs.sortedByDescending { it.date }.forEach { log ->
                    DailyLogItem(log = log)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "아직 기록이 없습니다.\n오늘의 기록을 시작해보세요!",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = " $unit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun MoodChart(
    logs: List<DailyLog>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                val width = size.width
                val height = size.height
                val padding = 20f

                if (logs.isEmpty()) return@Canvas

                val pointSpacing = (width - padding * 2) / (logs.size - 1).coerceAtLeast(1)

                // Draw mood line
                val moodPath = Path()
                logs.forEachIndexed { index, log ->
                    val x = padding + index * pointSpacing
                    val y = height - padding - ((log.mood - 1) / 4f) * (height - padding * 2)

                    if (index == 0) {
                        moodPath.moveTo(x, y)
                    } else {
                        moodPath.lineTo(x, y)
                    }
                }
                drawPath(moodPath, primaryColor, style = Stroke(width = 3f))

                // Draw craving line
                val cravingPath = Path()
                logs.forEachIndexed { index, log ->
                    val x = padding + index * pointSpacing
                    val y = height - padding - ((log.cravingLevel - 1) / 4f) * (height - padding * 2)

                    if (index == 0) {
                        cravingPath.moveTo(x, y)
                    } else {
                        cravingPath.lineTo(x, y)
                    }
                }
                drawPath(cravingPath, secondaryColor, style = Stroke(width = 3f))

                // Draw points
                logs.forEachIndexed { index, log ->
                    val x = padding + index * pointSpacing
                    val moodY = height - padding - ((log.mood - 1) / 4f) * (height - padding * 2)
                    val cravingY = height - padding - ((log.cravingLevel - 1) / 4f) * (height - padding * 2)

                    drawCircle(primaryColor, radius = 6f, center = Offset(x, moodY))
                    drawCircle(secondaryColor, radius = 6f, center = Offset(x, cravingY))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.height(12.dp).padding(end = 4.dp)) {
                        drawCircle(primaryColor, radius = 6f)
                    }
                    Text("기분", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.padding(horizontal = 16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.height(12.dp).padding(end = 4.dp)) {
                        drawCircle(secondaryColor, radius = 6f)
                    }
                    Text("음주욕구", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun DailyLogItem(log: DailyLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = log.date.format(DateTimeFormatter.ofPattern("M월 d일")),
                    style = MaterialTheme.typography.titleSmall
                )
                if (log.note.isNotEmpty()) {
                    Text(
                        text = log.note.take(30) + if (log.note.length > 30) "..." else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("기분", style = MaterialTheme.typography.labelSmall)
                    Text("${log.mood}/5", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("욕구", style = MaterialTheme.typography.labelSmall)
                    Text("${log.cravingLevel}/5", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
