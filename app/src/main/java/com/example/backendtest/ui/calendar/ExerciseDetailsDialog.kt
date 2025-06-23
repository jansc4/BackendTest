package com.example.backendtest.ui.calendar

import com.example.backendtest.data.model.ExercisePerformance
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ExerciseDetailsDialog(
    exercise: ExercisePerformance,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Exercise Details") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Exercise ID: ${exercise.exercise_id}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Duration: ${exercise.duration_min} min")
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Interval between: ${exercise.intervalBetween_days} days")
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Notes: ${exercise.notes}")
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Completed: ${if (exercise.done) "Yes" else "No"}")
            }
        },
        confirmButton = {
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDelete) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) {
                    Text("Close")
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
                exercise = ExercisePerformance(
                    id = "1",
                    exercise_id = "123",
                    duration_min = 45,
                    numberOfSets = 4,
                    numberOfRepetitions = 10,
                    weight = 60,
                    intervalBetween_days = 2,
                    notes = "Focus on form",
                    done = true
                ),
                onDismiss = {},
                onEdit = {},
                onDelete = {}
            )
        }
    }
}
