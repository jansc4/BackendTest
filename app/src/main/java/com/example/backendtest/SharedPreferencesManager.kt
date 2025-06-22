package com.example.backendtest

import android.content.Context
import android.content.SharedPreferences
import com.example.backendtest.data.UserPreferences

// SharedPreferencesManager.kt
class SharedPreferencesManager(private val context: Context) {
    private val PREFS_NAME = "StepCounterPrefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val KEY_EMAIL = "email"
        const val KEY_PASSWORD = "password"
        const val KEY_DAILY_GOAL = "daily_goal"
        const val KEY_REMEMBER_ME = "remember_me"
    }

    fun saveUserPreferences(userPreferences: UserPreferences) {
        prefs.edit().apply {
            putString(KEY_EMAIL, userPreferences.email)
            putString(KEY_PASSWORD, userPreferences.password)
            putInt(KEY_DAILY_GOAL, userPreferences.dailyStepsGoal)
            putBoolean(KEY_REMEMBER_ME, userPreferences.isRememberMeEnabled)
            apply()
        }
    }

    fun getUserPreferences(): UserPreferences {
        return UserPreferences(
            email = prefs.getString(KEY_EMAIL, "") ?: "",
            password = prefs.getString(KEY_PASSWORD, "") ?: "",
            dailyStepsGoal = prefs.getInt(KEY_DAILY_GOAL, 10000),
            isRememberMeEnabled = prefs.getBoolean(KEY_REMEMBER_ME, false)
        )
    }

    fun clearUserData() {
        prefs.edit().clear().apply()
    }
}