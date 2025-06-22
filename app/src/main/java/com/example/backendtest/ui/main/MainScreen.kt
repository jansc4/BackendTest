package com.example.backendtest.ui.main

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit
) {
    val stepsState by viewModel.stepsState.collectAsState()
    val currentSteps by viewModel.dailySteps.collectAsState()
    val dailyGoal by viewModel.dailyGoal.collectAsState()

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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Dzienne kroki",
                style = MaterialTheme.typography.headlineSmall
            )

            IconButton(onClick = onLogout) {
                Icon(Icons.Default.ExitToApp, "Wyloguj")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Steps Counter
        when (stepsState) {
            is StepsState.Loading -> {
                CircularProgressIndicator()
            }
            is StepsState.Success -> {
                // Kroki i cel
                Text(
                    text = "$currentSteps",
                    style = MaterialTheme.typography.displayLarge
                )

                Text(
                    text = "z celu $dailyGoal kroków",
                    style = MaterialTheme.typography.bodyLarge
                )

                // Progress Bar
                LinearProgressIndicator(
                    progress = (currentSteps.toFloat() / dailyGoal).coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
            is StepsState.Error -> {
                Text(
                    text = (stepsState as StepsState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}