package com.example.backendtest.data.model

import com.example.backendtest.data.network.ExerciseRequest
import com.example.backendtest.data.network.NewExerciseRequest

data class ExerciseData(
    val id: String? = null,
    val name: String = "",
    val description: String = "",
    val exerciseType: ExerciseType = ExerciseType.strength,
    val difficulty: DifficultyType = DifficultyType.easy,
    val video_url: String? = null,
    val thumbnail_url: String? = null
) {

    fun toNewExerciseRequest(): NewExerciseRequest {
        return NewExerciseRequest(
            name = name,
            description = description,
            exerciseType = exerciseType,
            difficulty = difficulty
        )
    }
    fun toExerciseRequest(): ExerciseRequest {
        return ExerciseRequest(
            name = name,
            description = description,
            exerciseType = exerciseType,
            video_url = video_url,
            thumbnail_url = thumbnail_url,
            difficulty = difficulty
        )
    }
}
