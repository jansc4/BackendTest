package com.example.backendtest.data.network

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
