package com.example.backendtest.data.network

import com.example.backendtest.data.model.DifficultyType
import com.example.backendtest.data.model.ExercisePerformanceData
import com.example.backendtest.data.model.ExerciseType
import java.time.LocalDate

data class RegisterRequest(val username: String, val email: String, val password: String)
data class RegisterResponse(val username: String, val email: String)

data class LoginResponse(val access_token: String, val refresh_token: String, val token_type: String)
data class MeResponse(val username: String, val email: String)


data class DailyStepsResponse(
    val steps: Int,
    val goal: Int,
    val date: String
)

data class UpdateStepsRequest(
    val steps: Int,
    val date: String = LocalDate.now().toString()
)

data class UpdateStepsResponse(
    val success: Boolean,
    val message: String
)

data class LoginRequest(
    val username: String,  // zmiana z email na username
    val password: String
)

data class NewExerciseRequest(
    val name: String,
    val description: String,
    val exerciseType: ExerciseType,
    val difficulty: DifficultyType
)

data class ExerciseRequest(
    val name: String,
    val description: String,
    val exerciseType: ExerciseType,
    val video_url: String?="",
    val thumbnail_url: String?="",
    val difficulty: DifficultyType
)


data class ExercisePerformanceRequest(
    val date: LocalDate,
    val exercises: List<ExercisePerformanceData>
)

data class CalendarDayResponse(
    val date: String,
    val steps: Int,
    val maxSteps: Int,
    val exercises: List<ExercisePerformanceData>,
    val id: String,
    val user_id: String
)