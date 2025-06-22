package com.example.backendtest.data


data class UserPreferences(
    val email: String = "",
    val password: String = "",
    val dailyStepsGoal: Int = 10000,
    val isRememberMeEnabled: Boolean = false
)

