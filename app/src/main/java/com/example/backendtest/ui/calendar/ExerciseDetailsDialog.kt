package com.example.backendtest.ui.calendar

import com.example.backendtest.data.model.ExercisePerformance
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.backendtest.data.model.ExercisePerformanceDisplay

@Composable
fun ExerciseDetailsDialog(
    exercise: ExercisePerformanceDisplay,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Szczegóły ćwiczenia") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = exercise.exerciseName, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Czas trwania: ${exercise.performance.duration_min} min")
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Powtarzanie: ${exercise.performance.intervalBetween_days} dni")
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Notatki: ${exercise.performance.notes}")
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Wykonano: ${if (exercise.performance.done) "Tak" else "Nie"}")
            }
        },
        confirmButton = {
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                TextButton(onClick = onEdit) {
                    Text("Edytuj")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDelete) {
                    Text("Usuń", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) {
                    Text("Zamknij")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ExerciseDetailsDialogPreview() {
    MaterialTheme {
        Surface {
            ExerciseDetailsDialog(
                exercise = ExercisePerformanceDisplay(
                    exerciseName = "martwy ciąg",
                    performance = ExercisePerformance(
                    id = "1",
                    exercise_id = "123",
                    duration_min = 45,
                    numberOfSets = 4,
                    numberOfRepetitions = 10,
                    weight = 60.0,
                    intervalBetween_days = 2,
                    notes = "Focus on form",
                    done = true
                )),
                onDismiss = {},
                onEdit = {},
                onDelete = {}
            )
        }
    }
}
