package com.example.backendtest.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.backendtest.data.model.ExercisePerformanceDisplay

@Composable
fun ExercisePerformanceCard(
    exercisePerformanceDisplay: ExercisePerformanceDisplay,
    onDoneToggle: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val perf = exercisePerformanceDisplay.performance
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = exercisePerformanceDisplay.exerciseName, style = MaterialTheme.typography.titleMedium)
                Text(text = perf.getPerformanceSummary(), style = MaterialTheme.typography.bodySmall)
            }
            Checkbox(
                checked = perf.done,
                onCheckedChange = onDoneToggle
            )
        }
    }
}
