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
import com.example.backendtest.data.model.Exercise
import com.example.backendtest.data.model.ExercisePerformance
import com.example.backendtest.data.model.ExercisePerformanceData
import com.example.backendtest.ui.components.HeaderText
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarExercisePerformanceForm(
    viewModel: CalendarViewModel,
    onDismiss: () -> Unit
) {
    val exercisePerformance by viewModel.selectedExercisePerformance.collectAsState()
    val exerciseList by viewModel.exerciseList.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    // Ustawienia lokalne pola formularza (stateful)
    var selectedExerciseId by remember { mutableStateOf(exercisePerformance?.exercise_id ?: viewModel.newPerformanceExerciseId.value ?: "") }
    var durationMin by remember { mutableStateOf(exercisePerformance?.duration_min?.toString() ?: "") }
    var numberOfSets by remember { mutableStateOf(exercisePerformance?.numberOfSets?.toString() ?: "") }
    var numberOfRepetitions by remember { mutableStateOf(exercisePerformance?.numberOfRepetitions?.toString() ?: "") }
    var weight by remember { mutableStateOf(exercisePerformance?.weight?.toString() ?: "") }
    var intervalDays by remember { mutableStateOf(exercisePerformance?.intervalBetween_days?.toString() ?: "") }
    var notes by remember { mutableStateOf(exercisePerformance?.notes ?: "") }
    var done by remember { mutableStateOf(exercisePerformance?.done ?: false) }

    // Dropdown
    var expanded by remember { mutableStateOf(false) }
    val selectedExerciseName = exerciseList.find { it.id == selectedExerciseId }?.name ?: "Wybierz ćwiczenie"

    val isEditMode = exercisePerformance != null
    val title = if (isEditMode) "Edytuj ćwiczenie" else "Zaplanuj ćwiczenie"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedExerciseName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Ćwiczenie") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                exerciseList.forEach { exercise ->
                    DropdownMenuItem(
                        text = { Text(exercise.name) },
                        onClick = {
                            selectedExerciseId = exercise.id
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = durationMin,
            onValueChange = { durationMin = it.filter { c -> c.isDigit() } },
            label = { Text("Czas trwania (minuty)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = numberOfSets,
            onValueChange = { numberOfSets = it.filter { c -> c.isDigit() } },
            label = { Text("Liczba serii") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = numberOfRepetitions,
            onValueChange = { numberOfRepetitions = it.filter { c -> c.isDigit() } },
            label = { Text("Powtórzenia w serii") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it.filter { c -> c.isDigit() } },
            label = { Text("Ciężar (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = intervalDays,
            onValueChange = { intervalDays = it.filter { c -> c.isDigit() } },
            label = { Text("Interwał (dni)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notatki") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            maxLines = 5
        )
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = done,
                onCheckedChange = { done = it }
            )
            Spacer(Modifier.width(8.dp))
            Text("Wykonano")
        }

        Spacer(Modifier.height(16.dp))

        Row {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (selectedExerciseId.isBlank()) return@Button

                    val data = ExercisePerformanceData(
                        id = exercisePerformance?.id ?: "",  // puste id jeśli nowy
                        exercise_id = selectedExerciseId,
                        duration_min = durationMin.toIntOrNull() ?: 0,
                        numberOfSets = numberOfSets.toIntOrNull() ?: 0,
                        numberOfRepetitions = numberOfRepetitions.toIntOrNull() ?: 0,
                        weight = weight.toDoubleOrNull() ?: 0.0,
                        intervalBetween_days = intervalDays.toIntOrNull() ?: 0,
                        notes = notes,
                        done = done
                    )
                    if (isEditMode) {
                        viewModel.updateExercisePerformance(data, selectedDate)
                    } else {
                        viewModel.addExerciseToCalendar(data, selectedDate)
                    }
                }
            ) {
                Text("Zapisz")
            }
            Spacer(Modifier.width(16.dp))
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    viewModel.clearSelectedExercise()
                    onDismiss()
                }
            ) {
                Text("Anuluj")
            }
        }
    }
}


//@Preview(showBackground = true, widthDp = 360, heightDp = 640)
//@Composable
//fun CalendarExercisePerformanceFormPreview() {
//    // Dummy ViewModel na potrzeby preview
//    val dummyViewModel = object : CalendarViewModel() {
//        override val exerciseList = MutableStateFlow(
//            listOf(
//                Exercise(id = "1", name = "Przysiad"),
//                Exercise(id = "2", name = "Wyciskanie"),
//                Exercise(id = "3", name = "Martwy ciąg"),
//            )
//        )
//
//        override val selectedExercisePerformance = MutableStateFlow(
//            ExercisePerformance(
//                id = "ep1",
//                exercise_id = "1",
//                duration_min = 30,
//                numberOfSets = 3,
//                numberOfRepetitions = 10,
//                weight = 50,
//                intervalBetween_days = 1,
//                notes = "Ćwiczenie na nogi",
//                done = false
//            )
//        )
//
//        override val selectedDate = MutableStateFlow(LocalDate.now())
//
//        override fun addExerciseToCalendar(data: ExercisePerformanceData) {
//            println("Dodajemy ćwiczenie: $data")
//        }
//
//        override fun updateExercisePerformance(data: ExercisePerformanceData, date: LocalDate) {
//            println("Aktualizujemy ćwiczenie: $data na dzień $date")
//        }
//
//        override fun clearSelectedExercise() {
//            selectedExercisePerformance.value = null
//        }
//
//        override fun closeAddDialog() {}
//    }
//
//    MaterialTheme {
//        CalendarExercisePerformanceForm(
//            viewModel = dummyViewModel,
//            onDismiss = {}
//        )
//    }
//}
//}

