package com.example.backendtest.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = viewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val performances by viewModel.performancesWithNames.collectAsState()
    val selectedExercise by viewModel.selectedExercisePerformance.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    LaunchedEffect(performances) {
        println("performancesWithNames size: ${performances.size}")
        performances.forEach {
            println("${it.exerciseName}, done: ${it.performance.done}")
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            DateNavigationBar(
                selectedDate = selectedDate,
                onPreviousDay = { viewModel.selectPreviousDay() },
                onNextDay = { viewModel.selectNextDay() }
            )

            if (performances.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Brak zaplanowanych ćwiczeń na ten dzień.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = performances,
                        key = { it.id }
                    ) { perfDisplay ->
                        ExercisePerformanceCard(
                            exercisePerformanceDisplay = perfDisplay,
                            onDoneToggle = { isDone -> viewModel.toggleDoneStatus(perfDisplay, isDone) },
                            onClick = { viewModel.selectExercise(perfDisplay) }
                        )
                    }
                }
            }
        }

        // FAB to add a new exercise
        FloatingActionButton(
            onClick = { viewModel.openAddDialog() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Dodaj ćwiczenie")
        }

        // Dialog for adding/editing exercise performance
        if (showAddDialog) {
            Dialog(onDismissRequest = { viewModel.closeAddDialog() }) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 8.dp,
                    modifier = Modifier.padding(16.dp)
                ) {
                    CalendarExercisePerformanceForm(
                        viewModel = viewModel,
                        onDismiss = { viewModel.closeAddDialog() }
                    )
                }
            }
        }

        // Dialog for showing exercise details
        selectedExercise?.let {
            ExerciseDetailsDialog(
                exercise = it,
                onDismiss = { viewModel.clearSelectedExercise() },
                onEdit = {
                    // Otwórz formularz edycji
                    viewModel.openAddDialog()
                },
                onDelete = {
                    viewModel.deleteExerciseFromCalendar(it.performance)
                    viewModel.clearSelectedExercise()
                }
            )
        }
    }
}
