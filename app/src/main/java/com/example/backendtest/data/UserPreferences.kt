package com.example.backendtest.data

data class UserPreferences(
    val email: String,
    val password: String,
    val dailyStepsGoal: Int,
    val isRememberMeEnabled: Boolean
)

