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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val stepsHistoryState by viewModel.stepsHistoryState.collectAsState()

    var showGoalSetter by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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
            Row {
                // Przycisk do ustawiania celu
                TextButton(onClick = { showGoalSetter = true }) {
                    Text("Ustaw cel")
                }
                // Przycisk wylogowania
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Wyloguj"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (showGoalSetter) {
            DailyGoalSetter(
                currentGoal = dailyGoal,
                onGoalChange = { newGoal ->
                    viewModel.setDailyGoal(newGoal)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showGoalSetter = false }) {
                Text("Zamknij")
            }
        } else {
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
                    StepsStatistics(history = history)
                }
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
fun StepsStatistics(history: List<StepHistoryEntry>) {
    val total = history.sumOf { it.steps }
    val average = if (history.isNotEmpty()) total / history.size else 0
    val max = history.maxOfOrNull { it.steps } ?: 0
    val min = history.minOfOrNull { it.steps } ?: 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Statystyki z ostatnich 7 dni", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatCard("Średnia", "$average")
            StatCard("Maks.", "$max")
            StatCard("Min.", "$min")
        }
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun DailyGoalSetter(
    currentGoal: Int,
    onGoalChange: (Int) -> Unit
) {
    var input by remember { mutableStateOf(currentGoal.toString()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Ustaw dzienny cel", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = input,
            onValueChange = {
                input = it
                it.toIntOrNull()?.let(onGoalChange)
            },
            label = { Text("Cel kroków") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
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
