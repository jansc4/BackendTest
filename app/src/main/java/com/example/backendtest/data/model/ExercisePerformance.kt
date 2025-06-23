package com.example.backendtest.data.model

import com.example.backendtest.data.network.ExercisePerformanceRequest

data class ExercisePerformance(
    val id: String,
    val exercise_id: String,
    var duration_min: Int,
    var numberOfSets: Int,
    var numberOfRepetitions: Int,
    var weight: Double,
    val intervalBetween_days: Int,
    val notes: String,
    val done: Boolean
){
    fun getPerformanceSummary(): String {
        val numberOfSetsStr = if ( numberOfSets > 1) "$numberOfSets sety" else "$numberOfSets set"
        val repsStr = if (numberOfRepetitions > 1) "$numberOfRepetitions powtórzenia" else "$numberOfRepetitions powtórzenie"
        val weightStr = "$weight kg"

        return when {
            numberOfSets > 0 && numberOfRepetitions > 0 && weight > 0 -> "$numberOfSetsStr po $repsStr z obciążeniem $weightStr"
            numberOfSets > 0 && numberOfRepetitions > 0 && weight == 0.0 -> "$numberOfSetsStr po $repsStr" // No weight
            numberOfSets == 0 && numberOfRepetitions > 0 -> "$repsStr" // if only reps present
            numberOfSets > 0 && numberOfRepetitions == 0 -> "$numberOfSetsStr" // if only numberOfSets present
            else -> "" // Fallback to original description if no structured data
        }
    }

}


data class ExercisePerformanceData(
    val id: String?="",
    val exercise_id: String,
    var duration_min: Int,
    var numberOfSets: Int,
    var numberOfRepetitions: Int,
    var weight: Double,
    val intervalBetween_days: Int,
    val notes: String,
    val done: Boolean
)