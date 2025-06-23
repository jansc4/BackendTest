package com.example.backendtest.ui.exercise

import com.example.backendtest.data.model.DifficultyType
import com.example.backendtest.data.model.Exercise
import com.example.backendtest.data.model.ExerciseType
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.min

val lengthOfDescription = 100

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val descriptionLength = min(exercise.description.length, lengthOfDescription)
    val shortDescription = exercise.description.substring(0, descriptionLength) +
            if (descriptionLength < exercise.description.length) "..." else ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Nagłówek z nazwą i ewentualną ikoną wideo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (!exercise.video_url.isNullOrBlank()) {
                    Icon(
                        imageVector = Icons.Sharp.PlayArrow,
                        contentDescription = "Wideo dostępne",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Opis
            Text(
                text = shortDescription,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Typ ćwiczenia
            Row {
                Text(
                    text = "Typ: ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = exercise.exerciseType.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Poziom trudności
            Row {
                Text(
                    text = "Trudność: ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = exercise.difficulty.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Akcje
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Sharp.Edit, contentDescription = "Edytuj")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edytuj")
                }
                TextButton(onClick = {
                    onDelete()
                    Toast.makeText(context, "Ćwiczenie zostało usunięte", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Usuń")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseCardPreview() {
    ExerciseCard(
        exercise = Exercise(
            id = "1",
            name = "Przysiady",
            description = "Świetne ćwiczenie na dolne partie ciała. Angażuje uda i pośladki.",
            exerciseType = ExerciseType.strength,
            difficulty = DifficultyType.easy,
            video_url = "https://example.com/video.mp4",
            thumbnail_url = null
        ),
        onEdit = {},
        onDelete = {},
        onClick = {}
    )
}
