package com.example.backendtest.ui.exercise

import com.example.backendtest.data.model.DifficultyType
import com.example.backendtest.data.model.ExerciseType


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.backendtest.data.model.ExerciseData
import com.example.backendtest.ui.components.HeaderText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseForm(
    exercise: ExerciseData? = null,
    onCancel: () -> Unit = {},
    viewModel: ExerciseViewModel = viewModel()
) {
    var name by remember { mutableStateOf(exercise?.name ?: "") }
    var description by remember { mutableStateOf(exercise?.description ?: "") }
    var exerciseType by remember { mutableStateOf(exercise?.exerciseType ?: ExerciseType.strength) }
    var difficultyType by remember { mutableStateOf(exercise?.difficulty ?: DifficultyType.easy) }
    var video_url by remember { mutableStateOf(exercise?.video_url ?: "") }

    var expandedExerciseType by remember { mutableStateOf(false) }
    var expandedDifficultyType by remember { mutableStateOf(false) }

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
            HeaderText("Ćwiczenie")
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nazwa ćwiczenia") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Opis") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 10,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expandedExerciseType,
                onExpandedChange = { expandedExerciseType = !expandedExerciseType },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = exerciseType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Typ ćwiczenia") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedExerciseType)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                DropdownMenu(
                    expanded = expandedExerciseType,
                    onDismissRequest = { expandedExerciseType = false }
                ) {
                    ExerciseType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                exerciseType = type
                                expandedExerciseType = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expandedDifficultyType,
                onExpandedChange = { expandedDifficultyType = !expandedDifficultyType },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = difficultyType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Poziom trudności") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDifficultyType)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                DropdownMenu(
                    expanded = expandedDifficultyType,
                    onDismissRequest = { expandedDifficultyType = false }
                ) {
                    DifficultyType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                difficultyType = type
                                expandedDifficultyType = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = video_url,
                onValueChange = { video_url = it },
                label = { Text("Link do wideo (opcjonalnie)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Przyciski
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Button(
                onClick = {
                    val data = ExerciseData(
                        id = exercise?.id,
                        name = name,
                        description = description,
                        exerciseType = exerciseType,
                        difficulty = difficultyType,
                        video_url = video_url,
                        thumbnail_url = ""
                    )
                    viewModel.addOrUpdate(data)
                    onCancel() // zamknięcie formularza
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Zapisz")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Anuluj")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseFormEditPreview() {
    MaterialTheme {
        Surface {
            ExerciseForm()
        }
    }
}
