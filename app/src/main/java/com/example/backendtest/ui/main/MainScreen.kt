package com.example.backendtest.ui.main

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.backendtest.data.network.StepHistoryEntry
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit
) {
    val stepsState by viewModel.stepsState.collectAsState()
    val currentSteps by viewModel.dailySteps.collectAsState()
    val dailyGoal by viewModel.dailyGoal.collectAsState()
    val history by viewModel.stepsHistory.collectAsState()
    val stepsHistoryState by viewModel.stepsHistoryState.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dzienne kroki",
                style = MaterialTheme.typography.headlineSmall
            )
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Wyloguj"
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        when (stepsState) {
            is StepsState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }
            is StepsState.Success -> {
                StepsDisplay(
                    currentSteps = currentSteps,
                    dailyGoal = dailyGoal
                )
            }
            is StepsState.Error -> {
                ErrorMessage(message = (stepsState as StepsState.Error).message)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        when (stepsHistoryState) {
            is StepsHistoryState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            is StepsHistoryState.Error -> {
                ErrorMessage(message = (stepsHistoryState as StepsHistoryState.Error).message)
            }
            is StepsHistoryState.Success -> {
                val history = (stepsHistoryState as StepsHistoryState.Success).history
                StepsChart(stepsHistory = history)

                // obliczenia pod wykresem
                val total = history.sumOf { it.steps }
                val average = if (history.isNotEmpty()) total / history.size else 0
                val max = history.maxOfOrNull { it.steps } ?: 0
                val min = history.minOfOrNull { it.steps } ?: 0

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Średnia: $average   Min: $min   Max: $max",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
            }
        }

    }
}

@Composable
fun StepsDisplay(
    currentSteps: Int,
    dailyGoal: Int
) {
    LaunchedEffect(currentSteps) {
        Log.d("UI", "Rekompozycja: kroki = $currentSteps")
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = currentSteps.toString(),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "z celu $dailyGoal kroków",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LinearProgressIndicator(
            progress = (currentSteps.toFloat() / dailyGoal).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Text(
            text = "${(currentSteps.toFloat() / dailyGoal * 100).toInt()}% celu",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

fun formatDateLabel(dateStr: String): String {
    return try {
        val date = LocalDate.parse(dateStr.substring(0, 10))
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pl"))
    } catch (e: Exception) {
        dateStr.takeLast(5)
    }
}

@Composable
fun StepsChart(stepsHistory: List<StepHistoryEntry>) {
    val barColor = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurface
    val maxSteps = stepsHistory.maxOfOrNull { it.steps }?.toFloat()?.coerceAtLeast(1f) ?: 1f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        Text(
            text = "Ostatnie 7 dni",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val barWidth = size.width / (stepsHistory.size * 2)
            val spacing = barWidth
            val chartHeight = size.height * 0.8f // zostawiamy miejsce na etykiety

            stepsHistory.forEachIndexed { index, entry ->
                val left = index * (barWidth + spacing) + spacing / 2
                val barHeight = (entry.steps / maxSteps) * chartHeight
                val top = size.height - barHeight - 20.dp.toPx()

                drawRect(
                    color = barColor,
                    topLeft = Offset(x = left, y = top),
                    size = Size(width = barWidth, height = barHeight)
                )

                // rysowanie daty (np. "06-23")
                drawContext.canvas.nativeCanvas.apply {
                    val label = formatDateLabel(entry.date)
                    drawText(
                        label,
                        left + barWidth / 2,
                        size.height,
                        android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            color = labelColor.toArgb()
                            textSize = 28f
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(16.dp)
    )
}
