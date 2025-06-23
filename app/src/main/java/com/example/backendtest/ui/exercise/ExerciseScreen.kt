package com.example.backendtest.ui.exercise

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.backendtest.data.model.Exercise
import com.example.backendtest.data.model.ExerciseType
import com.example.backendtest.ui.components.SearchBar // ← Użycie nowego komponentu SearchBar

@Composable
fun ExerciseScreen(
    modifier: Modifier = Modifier,
    viewModel: ExerciseViewModel = viewModel(),
    onExerciseClick: (Exercise) -> Unit
) {
    val exerciseState by viewModel.exerciseState.collectAsState()
    val selectedExerciseType by viewModel.selectedExerciseType.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val filteredList by viewModel.filteredExerciseList.collectAsState()


    var showExerciseDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<Exercise?>(null) }

    fun onSelectedExerciseTypeChange(type: ExerciseType?) {
        viewModel.selectExerciseType(type)
    }

    fun onClearSearchText() {
        viewModel.updateSearchText("")
    }

    fun onSearchTextChange(text: String) {
        viewModel.updateSearchText(text)
    }

    fun onSearchSubmit(query: String) {
        viewModel.updateSearchText(query)
        // Możesz dodać dodatkową logikę wyszukiwania, jeśli potrzeba
    }

    if (showExerciseDialog) {
        ExerciseForm(
            exercise = itemToEdit?.toExerciseData(),
            onCancel = { showExerciseDialog = false },
            viewModel = viewModel
        )
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        itemToEdit = null
                        showExerciseDialog = true
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Dodaj")
                }
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    SearchBar(
                        searchText = searchText,
                        onSearchTextChange = ::onSearchTextChange,
                        onClear = ::onClearSearchText,
                        onSearch = ::onSearchSubmit,
                        modifier = Modifier
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filtry ćwiczeń
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        FilterChip(
                            selected = selectedExerciseType == null,
                            onClick = { onSelectedExerciseTypeChange(null) },
                            label = { Text("Wszystkie") },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        ExerciseType.values().forEach { type ->
                            FilterChip(
                                selected = selectedExerciseType == type,
                                onClick = { onSelectedExerciseTypeChange(type) },
                                label = { Text(type.displayName) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lista ćwiczeń
                    when (exerciseState) {
                        is ExerciseState.Loading -> {
                            Text("Ładowanie ćwiczeń...")
                        }
                        is ExerciseState.Error -> {
                            Text("Błąd: ${(exerciseState as ExerciseState.Error).message}")
                        }
                        else -> {
                            LazyColumn {
                                items(filteredList, key = { it.id }) { exercise ->
                                    ExerciseCard(
                                        exercise = exercise,
                                        onDelete = { viewModel.deleteExercise(exercise.id) },
                                        onEdit = {
                                            itemToEdit = exercise
                                            showExerciseDialog = true
                                        },
                                        onClick = { onExerciseClick(exercise) }
                                    )
                                }
                                item {
                                    Spacer(modifier = Modifier.height(80.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
