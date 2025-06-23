package com.example.backendtest.data.model

data class Exercise(
    val id: String,
    val name: String,
    val description: String,
    val exerciseType: ExerciseType,
    val difficulty: DifficultyType,
    val video_url: String? = null,
    val thumbnail_url: String? = null
){
    fun toExerciseData(): ExerciseData {
        return ExerciseData(
            id = id,
            name = name,
            description = description,
            exerciseType = exerciseType,
            difficulty = difficulty,
            video_url = video_url,
            thumbnail_url = thumbnail_url
        )
    }
}
