package com.example.backendtest.data.network

import android.content.Context
import android.content.SharedPreferences

object UserSession {
    private lateinit var appContext: Context
    private const val PREF_NAME = "auth"
    private const val KEY_AUTH_TOKEN = "token"
    private const val KEY_AUTH_REFRESH_TOKEN = "refresh_token"

    var token: String? = null
        private set

    var refresh_token: String? = null
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
        // Wczytaj tokeny przy inicjalizacji
        getAuthToken()
        getAuthRefreshToken()
    }

    fun saveAuthToken(access_token: String, refresh_token: String) {
        val sharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString(KEY_AUTH_TOKEN, access_token)
            putString(KEY_AUTH_REFRESH_TOKEN, refresh_token)
            apply()
        }
        this.token = access_token
        this.refresh_token = refresh_token
    }

    private fun getAuthToken() {
        val sharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        token = sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    private fun getAuthRefreshToken() {
        val sharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        refresh_token = sharedPreferences.getString(KEY_AUTH_REFRESH_TOKEN, null)
    }

    fun clearSession() {
        val sharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        token = null
        refresh_token = null
    }
}