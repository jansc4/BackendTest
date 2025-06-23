package com.example.backendtest.data.network

data class RegisterRequest(val username: String, val email: String, val password: String)
data class RegisterResponse(val username: String, val email: String)

data class LoginResponse(val access_token: String, val refresh_token: String, val token_type: String)
data class MeResponse(val username: String, val email: String)

data class DailyStepsResponse(
    val steps: Int?,
    val maxSteps: Int?
)

data class UpdateStepsRequest(
    val steps: Int
)

data class UpdateStepsResponse(
    val steps: Int?,
    val maxSteps: Int?
)

data class TokenResponse(
    val access_token: String,
    val refresh_token: String?
)


data class StepHistoryEntry(
    val steps: Int,
    val date: String  // ISO 8601 format
)

data class CalendarEntry(
    val id: String,
    val date: String,  // ISO 8601 format
    val steps: Int?,
    val maxSteps: Int?,
    val exercises: List<ExercisePerformance> = emptyList()
)

data class ExercisePerformance(
    val id: String,
    val exercise_id: String,
    val duration_min: Int?,
    val numberOfSets: Int?,
    val numberOfRepetitions: Int?,
    val weight: Double?,
    val intervalBetween_days: Int?,
    val done: Boolean = false,
    val notes: String?
)

data class LoginRequest(
    val username: String,  // zmiana z email na username
    val password: String
)
