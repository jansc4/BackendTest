package com.example.backendtest.data.model

data class ExercisePerformanceDisplay(
    val performance: ExercisePerformance,
    val exerciseName: String
) {
    val id: String get() = performance.id
}