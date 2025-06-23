package com.example.backendtest.ui.calendar

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.backendtest.data.model.ExercisePerformance
import com.example.backendtest.data.model.ExercisePerformanceData
import com.example.backendtest.ui.components.HeaderText

@Composable
fun CalendarExercisePerformanceForm(
    exercisePerformance: ExercisePerformance? = null,
    onSave: (ExercisePerformanceData) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var exerciseId by remember { mutableStateOf(exercisePerformance?.exercise_id ?: "") }
    var durationMin by remember { mutableStateOf(exercisePerformance?.duration_min?.toString() ?: "") }
    var numberOfSets by remember { mutableStateOf(exercisePerformance?.numberOfSets?.toString() ?: "") }
    var numberOfRepetitions by remember { mutableStateOf(exercisePerformance?.numberOfRepetitions?.toString() ?: "") }
    var weight by remember { mutableStateOf(exercisePerformance?.weight?.toString() ?: "") }
    var intervalDays by remember { mutableStateOf(exercisePerformance?.intervalBetween_days?.toString() ?: "") }
    var notes by remember { mutableStateOf(exercisePerformance?.notes ?: "") }
    var done by remember { mutableStateOf(exercisePerformance?.done ?: false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderText("Exercise Performance")
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = exerciseId,
                onValueChange = { exerciseId = it },
                label = { Text("Exercise ID") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = durationMin,
                onValueChange = { durationMin = it },
                label = { Text("Duration (min)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = numberOfSets,
                onValueChange = { numberOfSets = it },
                label = { Text("Number of Sets") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = numberOfRepetitions,
                onValueChange = { numberOfRepetitions = it },
                label = { Text("Repetitions per Set") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = intervalDays,
                onValueChange = { intervalDays = it },
                label = { Text("Interval Between Days") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = done,
                    onCheckedChange = { done = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Done")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Button(
                onClick = {
                    val newPerformance = ExercisePerformanceData(
                        id = exercisePerformance?.id ?: "",
                        exercise_id = exerciseId,
                        duration_min = durationMin.toIntOrNull() ?: 0,
                        numberOfSets = numberOfSets.toIntOrNull() ?: 0,
                        numberOfRepetitions = numberOfRepetitions.toIntOrNull() ?: 0,
                        weight = weight.toIntOrNull() ?: 0,
                        intervalBetween_days = intervalDays.toIntOrNull() ?: 0,
                        notes = notes,
                        done = done
                    )
                    onSave(newPerformance)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarExercisePerformanceFormPreview() {
    MaterialTheme {
        Surface {
            CalendarExercisePerformanceForm(
                exercisePerformance = ExercisePerformance(
                    id = "1",
                    exercise_id = "123",
                    duration_min = 30,
                    numberOfSets = 4,
                    numberOfRepetitions = 12,
                    weight = 50,
                    intervalBetween_days = 2,
                    notes = "Keep back straight. Focus on breathing.",
                    done = true
                ),
                onSave = {},
                onCancel = {}
            )
        }
    }
}

